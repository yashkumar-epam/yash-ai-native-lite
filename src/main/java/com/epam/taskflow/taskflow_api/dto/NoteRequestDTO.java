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
public class NoteRequestDTO {

    @NotBlank(message = "Content is required")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;
}
