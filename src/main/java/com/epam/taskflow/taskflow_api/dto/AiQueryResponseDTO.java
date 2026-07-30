package com.epam.taskflow.taskflow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryResponseDTO {

    private String answer;
    private String model;
    private int contextFilesUsed;
}
