package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.TicketCreateRequestDTO;
import com.epam.taskflow.taskflow_api.dto.TicketResponseDTO;
import com.epam.taskflow.taskflow_api.dto.TicketStatusUpdateRequestDTO;
import com.epam.taskflow.taskflow_api.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support/tickets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SupportIQ — Tickets", description = "Customer support ticket management")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    @PostMapping
    @Operation(summary = "Create a new support ticket")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ticket created"),
        @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody @Valid TicketCreateRequestDTO request) {
        log.info("POST /api/support/tickets - Creating ticket for {}", request.getCustomerEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(request));
    }

    @GetMapping
    @Operation(summary = "Get all support tickets, optionally filtered by status")
    public ResponseEntity<List<TicketResponseDTO>> getTickets(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            log.info("GET /api/support/tickets?status={}", status);
            return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
        }
        log.info("GET /api/support/tickets - Fetching all tickets");
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/escalation-queue")
    @Operation(summary = "Get all tickets requiring escalation")
    public ResponseEntity<List<TicketResponseDTO>> getEscalationQueue() {
        log.info("GET /api/support/tickets/escalation-queue");
        return ResponseEntity.ok(ticketService.getEscalationQueue());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a support ticket by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket found"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Long id) {
        log.info("GET /api/support/tickets/{}", id);
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update ticket status and optionally assign an agent")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<TicketResponseDTO> updateStatus(@PathVariable Long id,
                                                          @RequestBody @Valid TicketStatusUpdateRequestDTO request) {
        log.info("PATCH /api/support/tickets/{}/status -> {}", id, request.getStatus());
        return ResponseEntity.ok(ticketService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a support ticket")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ticket deleted"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        log.info("DELETE /api/support/tickets/{}", id);
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
