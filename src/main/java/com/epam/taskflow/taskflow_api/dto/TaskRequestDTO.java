package com.epam.taskflow.taskflow_api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Pattern(regexp = "^(TODO|IN_PROGRESS|DONE)$", message = "Status must be one of: TODO, IN_PROGRESS, DONE")
    private String status;

    @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "Priority must be one of: LOW, MEDIUM, HIGH")
    private String priority;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;
}

