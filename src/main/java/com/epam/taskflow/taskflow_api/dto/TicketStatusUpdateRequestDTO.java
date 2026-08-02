package com.epam.taskflow.taskflow_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusUpdateRequestDTO {

    @NotBlank(message = "Status is required")
    private String status;

    private String assignedAgent;
}
