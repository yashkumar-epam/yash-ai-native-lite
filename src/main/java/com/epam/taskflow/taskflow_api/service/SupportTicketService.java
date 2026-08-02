package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.*;
import com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.epam.taskflow.taskflow_api.model.SupportTicket;
import com.epam.taskflow.taskflow_api.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportTicketService {

    private final SupportTicketRepository repository;
    private final AtomicLong ticketCounter = new AtomicLong(0);

    @Transactional
    public TicketResponseDTO createTicket(TicketCreateRequestDTO request) {
        long count = repository.count() + 1;
        String ticketNumber = String.format("TKT-%04d", count);
        SupportTicket ticket = SupportTicket.builder()
                .ticketNumber(ticketNumber)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .subject(request.getSubject())
                .body(request.getBody())
                .source(request.getSource() != null ? request.getSource() : "EMAIL")
                .build();
        log.info("Creating ticket {} for customer {}", ticketNumber, request.getCustomerEmail());
        return toDTO(repository.save(ticket));
    }

    public List<TicketResponseDTO> getAllTickets() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TicketResponseDTO> getTicketsByStatus(String status) {
        return repository.findByStatus(status.toUpperCase()).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TicketResponseDTO> getEscalationQueue() {
        return repository.findByEscalationRequired(true).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TicketResponseDTO getTicketById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public TicketResponseDTO updateStatus(Long id, TicketStatusUpdateRequestDTO request) {
        SupportTicket ticket = findOrThrow(id);
        ticket.setStatus(request.getStatus().toUpperCase());
        if (request.getAssignedAgent() != null) {
            ticket.setAssignedAgent(request.getAssignedAgent());
        }
        log.info("Updated ticket {} status to {}", ticket.getTicketNumber(), ticket.getStatus());
        return toDTO(repository.save(ticket));
    }

    @Transactional
    public void deleteTicket(Long id) {
        SupportTicket ticket = findOrThrow(id);
        log.info("Deleting ticket {}", ticket.getTicketNumber());
        repository.delete(ticket);
    }

    public SupportTicket findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found with id: " + id));
    }

    public TicketResponseDTO toDTO(SupportTicket t) {
        return TicketResponseDTO.builder()
                .id(t.getId())
                .ticketNumber(t.getTicketNumber())
                .customerName(t.getCustomerName())
                .customerEmail(t.getCustomerEmail())
                .subject(t.getSubject())
                .body(t.getBody())
                .category(t.getCategory())
                .status(t.getStatus())
                .priority(t.getPriority())
                .sentimentScore(t.getSentimentScore())
                .sentimentLabel(t.getSentimentLabel())
                .escalationRequired(t.getEscalationRequired())
                .source(t.getSource())
                .assignedAgent(t.getAssignedAgent())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
