package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.*;
import com.epam.taskflow.taskflow_api.exception.AiParsingException;
import com.epam.taskflow.taskflow_api.exception.GlobalExceptionHandler;
import com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.epam.taskflow.taskflow_api.service.SupportAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupportAiController Validation and AI Endpoint Tests")
class SupportAiControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SupportAiService supportAiService;

    @InjectMocks
    private SupportAiController controller;

    private TicketAnalysisResponseDTO sampleAnalysis;
    private DraftReplyResponseDTO sampleDraft;
    private SupportDashboardResponseDTO sampleDashboard;
    private BulkTriageResponseDTO sampleTriage;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();

        sampleAnalysis = TicketAnalysisResponseDTO.builder()
                .ticketId(1L)
                .ticketNumber("TKT-0001")
                .category("BILLING")
                .subcategory("Double charge")
                .sentimentScore(1)
                .sentimentLabel("VERY_ANGRY")
                .riskScore(9)
                .escalationRequired(true)
                .escalationReason("Customer threatened bank dispute and 50-seat cancellation")
                .suggestedPriority("CRITICAL")
                .keyIssues(List.of("Double charge", "Enterprise cancellation threat"))
                .recommendedAction("Immediate refund and manager callback")
                .estimatedResolutionTime("Same day")
                .sentimentAnalysis("Customer is extremely frustrated")
                .urgencyFactors(List.of("$2,400 double charge", "50-seat enterprise account"))
                .model("gpt-5-mini-2025-08-07")
                .build();

        sampleDraft = DraftReplyResponseDTO.builder()
                .ticketId(1L)
                .ticketNumber("TKT-0001")
                .subject("Re: Double billing — immediate resolution")
                .greeting("Dear Sarah,")
                .body("We sincerely apologize for the double charge. We have initiated an immediate full refund of $2,400.")
                .closing("Best regards, SupportIQ Team")
                .tone("APOLOGETIC")
                .includesRefundOffer(true)
                .keyPointsAddressed(List.of("Acknowledged double charge", "Committed to refund"))
                .suggestedFollowUpIn("2 hours")
                .model("gpt-5-mini-2025-08-07")
                .build();

        sampleDashboard = SupportDashboardResponseDTO.builder()
                .totalTickets(20)
                .openCount(8)
                .inProgressCount(4)
                .resolvedCount(4)
                .escalatedCount(2)
                .closedCount(2)
                .averageSentimentScore(5.2)
                .averageSentimentLabel("NEUTRAL")
                .criticalCount(2)
                .highPriorityCount(4)
                .queueHealthScore(62)
                .queueStatus("AT_RISK")
                .topIssues(List.of("Multiple billing incidents", "API rate limit confusion"))
                .aiRecommendations(List.of("Assign 2 agents to CRITICAL tickets immediately"))
                .escalationAlert("2 tickets require immediate manager attention")
                .model("gpt-5-mini-2025-08-07")
                .build();

        sampleTriage = BulkTriageResponseDTO.builder()
                .totalTriaged(3)
                .escalationCount(1)
                .triaged(List.of(
                    TriagedTicketDTO.builder()
                        .ticketId(1L).ticketNumber("TKT-0001").subject("Billing issue")
                        .urgencyRank(1).suggestedPriority("CRITICAL")
                        .urgencyReason("Enterprise churn risk").escalationRequired(true).build()
                ))
                .overallInsight("Critical billing issue must be resolved first")
                .model("gpt-5-mini-2025-08-07")
                .build();
    }

    // ─── POST /api/support/ai/analyze ────────────────────────────────────────

    @Test
    @DisplayName("Should analyze raw text and return 200 OK")
    void analyzeRaw_shouldReturn200_whenValidText() throws Exception {
        when(supportAiService.analyzeRaw(any())).thenReturn(sampleAnalysis);

        RawAnalysisRequestDTO request = RawAnalysisRequestDTO.builder()
                .rawText("Customer email text here")
                .build();

        mockMvc.perform(post("/api/support/ai/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("BILLING"))
                .andExpect(jsonPath("$.sentimentScore").value(1))
                .andExpect(jsonPath("$.escalationRequired").value(true))
                .andExpect(jsonPath("$.model").value("gpt-5-mini-2025-08-07"));
    }

    @Test
    @DisplayName("Should reject blank rawText - returns 400")
    void analyzeRaw_shouldReturn400_whenRawTextIsBlank() throws Exception {
        RawAnalysisRequestDTO request = RawAnalysisRequestDTO.builder()
                .rawText("   ")
                .build();

        mockMvc.perform(post("/api/support/ai/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Text is required")));
    }

    @Test
    @DisplayName("Should reject rawText exceeding 5000 characters - returns 400")
    void analyzeRaw_shouldReturn400_whenRawTextExceeds5000Chars() throws Exception {
        RawAnalysisRequestDTO request = RawAnalysisRequestDTO.builder()
                .rawText("a".repeat(5001))
                .build();

        mockMvc.perform(post("/api/support/ai/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Text cannot exceed 5000 characters")));
    }

    @Test
    @DisplayName("Should return 502 when AI parsing fails for analyzeRaw")
    void analyzeRaw_shouldReturn502_whenAiParsingFails() throws Exception {
        when(supportAiService.analyzeRaw(any()))
                .thenThrow(new AiParsingException("No JSON found in AI response"));

        RawAnalysisRequestDTO request = RawAnalysisRequestDTO.builder()
                .rawText("Some customer email")
                .build();

        mockMvc.perform(post("/api/support/ai/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502));
    }

    // ─── POST /api/support/tickets/{id}/analyze ──────────────────────────────

    @Test
    @DisplayName("Should analyze ticket by ID and return 200 OK")
    void analyzeTicket_shouldReturn200_whenTicketExists() throws Exception {
        when(supportAiService.analyzeTicket(1L)).thenReturn(sampleAnalysis);

        mockMvc.perform(post("/api/support/tickets/1/analyze"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value("TKT-0001"))
                .andExpect(jsonPath("$.riskScore").value(9));
    }

    @Test
    @DisplayName("Should return 404 when analyzing non-existent ticket")
    void analyzeTicket_shouldReturn404_whenTicketNotFound() throws Exception {
        when(supportAiService.analyzeTicket(99L))
                .thenThrow(new ResourceNotFoundException("Support ticket not found with id: 99"));

        mockMvc.perform(post("/api/support/tickets/99/analyze"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ─── POST /api/support/tickets/{id}/draft-reply ──────────────────────────

    @Test
    @DisplayName("Should draft reply and return 200 OK")
    void draftReply_shouldReturn200_withDraftedReply() throws Exception {
        when(supportAiService.draftReply(1L)).thenReturn(sampleDraft);

        mockMvc.perform(post("/api/support/tickets/1/draft-reply"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tone").value("APOLOGETIC"))
                .andExpect(jsonPath("$.includesRefundOffer").value(true))
                .andExpect(jsonPath("$.greeting").value("Dear Sarah,"));
    }

    @Test
    @DisplayName("Should return 404 when drafting reply for non-existent ticket")
    void draftReply_shouldReturn404_whenTicketNotFound() throws Exception {
        when(supportAiService.draftReply(99L))
                .thenThrow(new ResourceNotFoundException("Support ticket not found with id: 99"));

        mockMvc.perform(post("/api/support/tickets/99/draft-reply"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /api/support/dashboard ──────────────────────────────────────────

    @Test
    @DisplayName("Should return dashboard with AI health score - 200 OK")
    void getDashboard_shouldReturn200_withQueueMetrics() throws Exception {
        when(supportAiService.getDashboard()).thenReturn(sampleDashboard);

        mockMvc.perform(get("/api/support/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queueHealthScore").value(62))
                .andExpect(jsonPath("$.queueStatus").value("AT_RISK"))
                .andExpect(jsonPath("$.totalTickets").value(20))
                .andExpect(jsonPath("$.escalatedCount").value(2));
    }

    @Test
    @DisplayName("Should return 502 when AI parsing fails for dashboard")
    void getDashboard_shouldReturn502_whenAiParsingFails() throws Exception {
        when(supportAiService.getDashboard())
                .thenThrow(new AiParsingException("Malformed JSON in AI response"));

        mockMvc.perform(get("/api/support/dashboard"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message", containsString("unparseable")));
    }

    // ─── POST /api/support/tickets/bulk-triage ───────────────────────────────

    @Test
    @DisplayName("Should bulk triage tickets and return 200 OK")
    void bulkTriage_shouldReturn200_withRankedTickets() throws Exception {
        when(supportAiService.bulkTriage(any())).thenReturn(sampleTriage);

        BulkTriageRequestDTO request = BulkTriageRequestDTO.builder()
                .ticketIds(List.of(1L, 2L, 3L))
                .build();

        mockMvc.perform(post("/api/support/tickets/bulk-triage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTriaged").value(3))
                .andExpect(jsonPath("$.escalationCount").value(1))
                .andExpect(jsonPath("$.triaged[0].urgencyRank").value(1));
    }

    @Test
    @DisplayName("Should reject bulk triage with only 1 ticket ID - returns 400")
    void bulkTriage_shouldReturn400_whenOnlyOneTicketId() throws Exception {
        BulkTriageRequestDTO request = BulkTriageRequestDTO.builder()
                .ticketIds(List.of(1L))
                .build();

        mockMvc.perform(post("/api/support/tickets/bulk-triage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("2 and 50")));
    }

    @Test
    @DisplayName("Should reject bulk triage with null ticketIds - returns 400")
    void bulkTriage_shouldReturn400_whenTicketIdsIsNull() throws Exception {
        BulkTriageRequestDTO request = BulkTriageRequestDTO.builder()
                .ticketIds(null)
                .build();

        mockMvc.perform(post("/api/support/tickets/bulk-triage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Ticket IDs are required")));
    }
}
