package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.*;
import com.epam.taskflow.taskflow_api.service.SupportAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SupportIQ — AI Intelligence", description = "AI-powered support intelligence: classify, sentiment, risk, drafts, triage")
public class SupportAiController {

    private final SupportAiService supportAiService;

    @PostMapping("/ai/analyze")
    @Operation(
        summary = "Analyze raw text (HERO endpoint)",
        description = "Paste any raw customer email or message. AI returns: category, sentiment score, risk score, escalation decision, and recommended action. No ticket needed."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Analysis complete"),
        @ApiResponse(responseCode = "400", description = "Request body invalid"),
        @ApiResponse(responseCode = "502", description = "AI returned unparseable response")
    })
    public ResponseEntity<TicketAnalysisResponseDTO> analyzeRaw(@RequestBody @Valid RawAnalysisRequestDTO request) {
        log.info("POST /api/support/ai/analyze - raw text analysis ({} chars)", request.getRawText().length());
        return ResponseEntity.ok(supportAiService.analyzeRaw(request));
    }

    @PostMapping("/tickets/{id}/analyze")
    @Operation(
        summary = "Analyze an existing ticket",
        description = "AI performs deep analysis on a stored ticket and persists the results (sentiment, category, priority, escalation flag) back to the ticket."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Analysis complete and ticket updated"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
        @ApiResponse(responseCode = "502", description = "AI returned unparseable response")
    })
    public ResponseEntity<TicketAnalysisResponseDTO> analyzeTicket(@PathVariable Long id) {
        log.info("POST /api/support/tickets/{}/analyze", id);
        return ResponseEntity.ok(supportAiService.analyzeTicket(id));
    }

    @PostMapping("/tickets/{id}/draft-reply")
    @Operation(
        summary = "Generate AI-drafted reply",
        description = "AI generates a professional, empathetic reply to the customer's ticket — including greeting, body, closing, tone classification, and key points addressed."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Draft reply generated"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
        @ApiResponse(responseCode = "502", description = "AI returned unparseable response")
    })
    public ResponseEntity<DraftReplyResponseDTO> draftReply(@PathVariable Long id) {
        log.info("POST /api/support/tickets/{}/draft-reply", id);
        return ResponseEntity.ok(supportAiService.draftReply(id));
    }

    @GetMapping("/dashboard")
    @Operation(
        summary = "AI-powered support dashboard",
        description = "Computes live queue statistics, then uses AI to assess queue health (0-100 score), identify top issues, and generate actionable recommendations for management."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard generated"),
        @ApiResponse(responseCode = "502", description = "AI returned unparseable response")
    })
    public ResponseEntity<SupportDashboardResponseDTO> getDashboard() {
        log.info("GET /api/support/dashboard");
        return ResponseEntity.ok(supportAiService.getDashboard());
    }

    @PostMapping("/tickets/bulk-triage")
    @Operation(
        summary = "Bulk triage — AI urgency ranking",
        description = "Send 2–50 ticket IDs. AI ranks them by urgency from most to least critical, assigns suggested priority, and explains each ranking decision."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Triage complete"),
        @ApiResponse(responseCode = "400", description = "Validation failed (need 2-50 ticket IDs)"),
        @ApiResponse(responseCode = "502", description = "AI returned unparseable response")
    })
    public ResponseEntity<BulkTriageResponseDTO> bulkTriage(@RequestBody @Valid BulkTriageRequestDTO request) {
        log.info("POST /api/support/tickets/bulk-triage - {} tickets", request.getTicketIds().size());
        return ResponseEntity.ok(supportAiService.bulkTriage(request));
    }
}
