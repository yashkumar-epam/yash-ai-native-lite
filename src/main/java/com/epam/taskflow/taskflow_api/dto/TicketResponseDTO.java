package com.epam.taskflow.taskflow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDTO {

    private Long id;
    private String ticketNumber;
    private String customerName;
    private String customerEmail;
    private String subject;
    private String body;
    private String category;
    private String status;
    private String priority;
    private Integer sentimentScore;
    private String sentimentLabel;
    private Boolean escalationRequired;
    private String source;
    private String assignedAgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
