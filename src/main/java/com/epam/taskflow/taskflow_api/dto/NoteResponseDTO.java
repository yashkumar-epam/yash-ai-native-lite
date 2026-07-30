package com.epam.taskflow.taskflow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDTO {

    private Long id;
    private Long taskId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
