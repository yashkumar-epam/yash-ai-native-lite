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
public class TicketAnalysisResponseDTO {

    private Long ticketId;
    private String ticketNumber;
    private String category;
    private String subcategory;
    private int sentimentScore;
    private String sentimentLabel;
    private int riskScore;
    private boolean escalationRequired;
    private String escalationReason;
    private String suggestedPriority;
    private List<String> keyIssues;
    private String recommendedAction;
    private String estimatedResolutionTime;
    private String sentimentAnalysis;
    private List<String> urgencyFactors;
    private String model;
}
