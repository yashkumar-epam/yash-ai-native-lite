package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.*;
import com.epam.taskflow.taskflow_api.model.SupportTicket;
import com.epam.taskflow.taskflow_api.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportAiService {

    private static final String ANALYSIS_SYSTEM_PROMPT =
        "You are an expert customer support intelligence AI. Analyze the customer message and provide a comprehensive intelligence report.\n\n" +
        "CATEGORY must be one of: BILLING, TECHNICAL, ACCOUNT, COMPLAINT, REFUND, GENERAL\n" +
        "SENTIMENT SCORE: 1 (VERY_ANGRY) to 10 (VERY_SATISFIED). Use: 1-2=VERY_ANGRY, 3-4=ANGRY, 5-6=NEUTRAL, 7-8=SATISFIED, 9-10=VERY_SATISFIED\n" +
        "RISK SCORE: 1-10 (10=highest risk of churn/escalation/legal action)\n" +
        "PRIORITY: LOW, MEDIUM, HIGH, or CRITICAL\n" +
        "Set escalationRequired=true if: sentiment<=3, risk>=7, threats of legal action, churn risk, VIP customer, or regulatory complaint threatened.\n\n" +
        "Respond ONLY with valid JSON — no markdown, no explanation — matching exactly this schema:\n" +
        "{\"category\":string,\"subcategory\":string,\"sentimentScore\":int,\"sentimentLabel\":string," +
        "\"riskScore\":int,\"escalationRequired\":boolean,\"escalationReason\":string|null," +
        "\"suggestedPriority\":string,\"keyIssues\":[string],\"recommendedAction\":string," +
        "\"estimatedResolutionTime\":string,\"sentimentAnalysis\":string,\"urgencyFactors\":[string]}";

    private static final String DRAFT_REPLY_SYSTEM_PROMPT =
        "You are an expert customer support writer. Draft a professional, empathetic reply to the customer ticket.\n\n" +
        "TONE must be one of: APOLOGETIC, EMPATHETIC, INFORMATIONAL, PROFESSIONAL\n" +
        "Choose APOLOGETIC for billing errors or service failures. EMPATHETIC for frustrated customers. " +
        "INFORMATIONAL for how-to questions. PROFESSIONAL for general inquiries.\n" +
        "The reply must: acknowledge the specific issue, show genuine empathy, offer a clear resolution path, " +
        "and end with a commitment to follow up. Never be generic. Reference specific details from the ticket.\n\n" +
        "Respond ONLY with valid JSON — no markdown, no explanation — matching exactly this schema:\n" +
        "{\"subject\":string,\"greeting\":string,\"body\":string,\"closing\":string," +
        "\"tone\":string,\"includesRefundOffer\":boolean,\"keyPointsAddressed\":[string],\"suggestedFollowUpIn\":string}";

    private static final String DASHBOARD_SYSTEM_PROMPT =
        "You are a customer support operations AI analyst. Given queue statistics, produce a health assessment.\n\n" +
        "QUEUE HEALTH SCORE: 0-100 (100=excellent). Penalize for: high escalation rate, low sentiment, " +
        "many open tickets, many CRITICAL/HIGH tickets, unresolved tickets.\n" +
        "QUEUE STATUS: HEALTHY (score>=75), AT_RISK (50-74), CRITICAL (<50)\n" +
        "Identify real, specific issues based on the data provided.\n\n" +
        "Respond ONLY with valid JSON — no markdown, no explanation — matching exactly this schema:\n" +
        "{\"queueHealthScore\":int,\"queueStatus\":string,\"topIssues\":[string]," +
        "\"aiRecommendations\":[string],\"escalationAlert\":string|null}";

    private static final String BULK_TRIAGE_SYSTEM_PROMPT =
        "You are an expert customer support triage AI. Rank the given tickets by urgency from most to least critical.\n\n" +
        "Consider: sentiment score (angry customers first), risk of churn, issue severity, SLA implications, " +
        "whether escalation is needed, and business impact.\n" +
        "Rank 1 = most urgent. Set escalationRequired=true for tickets needing immediate manager attention.\n\n" +
        "Respond ONLY with valid JSON — no markdown, no explanation — matching exactly this schema:\n" +
        "{\"triaged\":[{\"ticketId\":long,\"ticketNumber\":string,\"subject\":string,\"urgencyRank\":int," +
        "\"suggestedPriority\":string,\"urgencyReason\":string,\"escalationRequired\":boolean}]," +
        "\"overallInsight\":string}";

    private final SupportTicketRepository ticketRepository;
    private final SupportTicketService ticketService;
    private final DialGateway dialGateway;
    private final AiResponseParser parser;

    public TicketAnalysisResponseDTO analyzeRaw(RawAnalysisRequestDTO request) {
        log.info("Analyzing raw support text ({} chars)", request.getRawText().length());
        String rawJson = dialGateway.chat(ANALYSIS_SYSTEM_PROMPT, request.getRawText());
        TicketAnalysisResponseDTO result = parser.parse(rawJson, TicketAnalysisResponseDTO.class);
        result.setModel(dialGateway.getModel());
        return result;
    }

    @Transactional
    public TicketAnalysisResponseDTO analyzeTicket(Long ticketId) {
        SupportTicket ticket = ticketService.findOrThrow(ticketId);
        log.info("Analyzing ticket {} ({})", ticket.getTicketNumber(), ticket.getSubject());

        String input = buildTicketContext(ticket);
        String rawJson = dialGateway.chat(ANALYSIS_SYSTEM_PROMPT, input);
        TicketAnalysisResponseDTO result = parser.parse(rawJson, TicketAnalysisResponseDTO.class);
        result.setTicketId(ticketId);
        result.setTicketNumber(ticket.getTicketNumber());
        result.setModel(dialGateway.getModel());

        // Persist AI insights back onto the ticket
        ticket.setSentimentScore(result.getSentimentScore());
        ticket.setSentimentLabel(result.getSentimentLabel());
        ticket.setCategory(result.getCategory());
        ticket.setPriority(result.getSuggestedPriority());
        ticket.setEscalationRequired(result.isEscalationRequired());
        if (result.isEscalationRequired() && "OPEN".equals(ticket.getStatus())) {
            ticket.setStatus("ESCALATED");
        }
        ticketRepository.save(ticket);

        return result;
    }

    public DraftReplyResponseDTO draftReply(Long ticketId) {
        SupportTicket ticket = ticketService.findOrThrow(ticketId);
        log.info("Drafting reply for ticket {}", ticket.getTicketNumber());

        String input = buildTicketContext(ticket);
        String rawJson = dialGateway.chat(DRAFT_REPLY_SYSTEM_PROMPT, input);
        DraftReplyResponseDTO result = parser.parse(rawJson, DraftReplyResponseDTO.class);
        result.setTicketId(ticketId);
        result.setTicketNumber(ticket.getTicketNumber());
        result.setModel(dialGateway.getModel());
        return result;
    }

    public SupportDashboardResponseDTO getDashboard() {
        log.info("Generating support dashboard");
        List<SupportTicket> all = ticketRepository.findAll();

        long openCount = all.stream().filter(t -> "OPEN".equals(t.getStatus())).count();
        long inProgressCount = all.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
        long resolvedCount = all.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count();
        long escalatedCount = all.stream().filter(t -> "ESCALATED".equals(t.getStatus())).count();
        long closedCount = all.stream().filter(t -> "CLOSED".equals(t.getStatus())).count();
        long criticalCount = all.stream().filter(t -> "CRITICAL".equals(t.getPriority())).count();
        long highCount = all.stream().filter(t -> "HIGH".equals(t.getPriority())).count();

        double avgSentiment = all.stream()
                .filter(t -> t.getSentimentScore() != null)
                .mapToInt(SupportTicket::getSentimentScore)
                .average().orElse(5.0);

        Map<String, Long> categoryBreakdown = all.stream()
                .collect(Collectors.groupingBy(SupportTicket::getCategory, Collectors.counting()));

        String statsContext = String.format(
            "Support Queue Statistics:\n" +
            "Total tickets: %d | Open: %d | In Progress: %d | Resolved: %d | Escalated: %d | Closed: %d\n" +
            "Critical priority: %d | High priority: %d\n" +
            "Average sentiment score: %.1f/10\n" +
            "Category breakdown: %s\n" +
            "Escalation rate: %.0f%%\n" +
            "Resolution rate: %.0f%%",
            all.size(), openCount, inProgressCount, resolvedCount, escalatedCount, closedCount,
            criticalCount, highCount, avgSentiment,
            categoryBreakdown.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", ")),
            all.isEmpty() ? 0 : (100.0 * escalatedCount / all.size()),
            all.isEmpty() ? 0 : (100.0 * (resolvedCount + closedCount) / all.size())
        );

        String rawJson = dialGateway.chat(DASHBOARD_SYSTEM_PROMPT, statsContext);
        DashboardAiDTO aiResult = parser.parse(rawJson, DashboardAiDTO.class);

        return SupportDashboardResponseDTO.builder()
                .totalTickets(all.size())
                .openCount(openCount)
                .inProgressCount(inProgressCount)
                .resolvedCount(resolvedCount)
                .escalatedCount(escalatedCount)
                .closedCount(closedCount)
                .averageSentimentScore(Math.round(avgSentiment * 10.0) / 10.0)
                .averageSentimentLabel(sentimentLabel(avgSentiment))
                .criticalCount(criticalCount)
                .highPriorityCount(highCount)
                .categoryBreakdown(categoryBreakdown)
                .queueHealthScore(aiResult.getQueueHealthScore())
                .queueStatus(aiResult.getQueueStatus())
                .topIssues(aiResult.getTopIssues())
                .aiRecommendations(aiResult.getAiRecommendations())
                .escalationAlert(aiResult.getEscalationAlert())
                .model(dialGateway.getModel())
                .build();
    }

    public BulkTriageResponseDTO bulkTriage(BulkTriageRequestDTO request) {
        List<SupportTicket> tickets = ticketRepository.findAllById(request.getTicketIds());
        if (tickets.isEmpty()) {
            throw new com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException(
                    "No tickets found for the provided IDs");
        }
        log.info("Bulk-triaging {} tickets", tickets.size());

        StringBuilder sb = new StringBuilder("Support tickets to triage:\n");
        tickets.forEach(t -> sb.append(String.format(
            "Ticket %s (id=%d): \"%s\" | Status: %s | Priority: %s | Sentiment: %s\nBody: %s\n\n",
            t.getTicketNumber(), t.getId(), t.getSubject(), t.getStatus(),
            t.getPriority(), t.getSentimentLabel() != null ? t.getSentimentLabel() : "UNKNOWN",
            t.getBody().length() > 200 ? t.getBody().substring(0, 200) + "..." : t.getBody()
        )));

        String rawJson = dialGateway.chat(BULK_TRIAGE_SYSTEM_PROMPT, sb.toString());
        BulkTriageInternalDTO internal = parser.parse(rawJson, BulkTriageInternalDTO.class);

        List<TriagedTicketDTO> triaged = internal.getTriaged() != null ? internal.getTriaged() : List.of();
        long escalationCount = triaged.stream().filter(TriagedTicketDTO::isEscalationRequired).count();

        return BulkTriageResponseDTO.builder()
                .totalTriaged(triaged.size())
                .escalationCount((int) escalationCount)
                .triaged(triaged)
                .overallInsight(internal.getOverallInsight())
                .model(dialGateway.getModel())
                .build();
    }

    private String buildTicketContext(SupportTicket ticket) {
        return String.format(
            "Ticket: %s\nCustomer: %s (%s)\nSubject: %s\nSource: %s\nStatus: %s\nPriority: %s\n\nMessage:\n%s",
            ticket.getTicketNumber(), ticket.getCustomerName(), ticket.getCustomerEmail(),
            ticket.getSubject(), ticket.getSource(), ticket.getStatus(), ticket.getPriority(),
            ticket.getBody()
        );
    }

    private String sentimentLabel(double score) {
        if (score <= 2) return "VERY_ANGRY";
        if (score <= 4) return "ANGRY";
        if (score <= 6) return "NEUTRAL";
        if (score <= 8) return "SATISFIED";
        return "VERY_SATISFIED";
    }

    // Internal parsing DTOs
    @lombok.Data
    private static class DashboardAiDTO {
        private int queueHealthScore;
        private String queueStatus;
        private List<String> topIssues;
        private List<String> aiRecommendations;
        private String escalationAlert;
    }

    @lombok.Data
    private static class BulkTriageInternalDTO {
        private List<TriagedTicketDTO> triaged;
        private String overallInsight;
    }
}
