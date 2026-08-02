package com.epam.taskflow.taskflow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportDashboardResponseDTO {

    private long totalTickets;
    private long openCount;
    private long inProgressCount;
    private long resolvedCount;
    private long escalatedCount;
    private long closedCount;
    private double averageSentimentScore;
    private String averageSentimentLabel;
    private long criticalCount;
    private long highPriorityCount;
    private Map<String, Long> categoryBreakdown;
    private int queueHealthScore;
    private String queueStatus;
    private List<String> topIssues;
    private List<String> aiRecommendations;
    private String escalationAlert;
    private String model;
}
