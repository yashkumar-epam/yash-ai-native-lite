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
public class BulkTriageResponseDTO {

    private int totalTriaged;
    private int escalationCount;
    private List<TriagedTicketDTO> triaged;
    private String overallInsight;
    private String model;
}
