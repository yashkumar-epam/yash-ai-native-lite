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
public class RawAnalysisRequestDTO {

    @NotBlank(message = "Text is required")
    @Size(max = 5000, message = "Text cannot exceed 5000 characters")
    private String rawText;
}
