package com.epam.taskflow.taskflow_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryRequestDTO {

    @NotBlank(message = "Question is required")
    @Size(max = 1000, message = "Question cannot exceed 1000 characters")
    private String question;
}
