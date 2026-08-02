package com.epam.taskflow.taskflow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftReplyResponseDTO {

    private Long ticketId;
    private String ticketNumber;
    private String subject;
    private String greeting;
    private String body;
    private String closing;
    private String tone;
    private boolean includesRefundOffer;
    private List<String> keyPointsAddressed;
    private String suggestedFollowUpIn;
    private String model;
}
