package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.AiQueryRequestDTO;
import com.epam.taskflow.taskflow_api.dto.AiQueryResponseDTO;
import com.epam.taskflow.taskflow_api.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI Query", description = "RAG-powered codebase Q&A using Claude")
public class AiController {

    private final AiService aiService;

    @PostMapping("/ask")
    @Operation(summary = "Ask a question about the TaskFlow codebase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question answered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid question"),
            @ApiResponse(responseCode = "503", description = "AI service unavailable")
    })
    public ResponseEntity<AiQueryResponseDTO> ask(@RequestBody @Valid AiQueryRequestDTO request) {
        log.info("POST /api/ai/ask - question length: {}", request.getQuestion().length());
        AiQueryResponseDTO response = aiService.askQuestion(request);
        return ResponseEntity.ok(response);
    }
}
