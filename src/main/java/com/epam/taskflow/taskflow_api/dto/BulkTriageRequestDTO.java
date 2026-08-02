package com.epam.taskflow.taskflow_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkTriageRequestDTO {

    @NotNull(message = "Ticket IDs are required")
    @Size(min = 2, max = 50, message = "Provide between 2 and 50 ticket IDs")
    private List<Long> ticketIds;
}
