package com.epam.taskflow.taskflow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriagedTicketDTO {

    private Long ticketId;
    private String ticketNumber;
    private String subject;
    private int urgencyRank;
    private String suggestedPriority;
    private String urgencyReason;
    private boolean escalationRequired;
}
