package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.AiQueryRequestDTO;
import com.epam.taskflow.taskflow_api.dto.AiQueryResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AiService {

    private static final int MAX_CONTEXT_FILES = 20;
    private static final int MAX_CONTEXT_CHARS = 50_000;
    private static final String SYSTEM_PROMPT =
            "You are an expert on the TaskFlow Spring Boot REST API codebase. " +
            "Answer questions about the code accurately and concisely. " +
            "Base your answers on the provided Java source files.";

    private final DialGateway dialGateway;

    public AiService(DialGateway dialGateway) {
        this.dialGateway = dialGateway;
    }

    public AiQueryResponseDTO askQuestion(AiQueryRequestDTO request) {
        log.info("Processing AI question: {}", request.getQuestion());
        List<Path> sourceFiles = loadSourceFilePaths();
        String context = buildContext(sourceFiles);
        String userMessage = context.isBlank()
                ? request.getQuestion()
                : "Context from the codebase:\n\n" + context + "\nQuestion: " + request.getQuestion();
        String answer = dialGateway.chat(SYSTEM_PROMPT, userMessage);
        return AiQueryResponseDTO.builder()
                .answer(answer)
                .model(dialGateway.getModel())
                .contextFilesUsed(sourceFiles.size())
                .build();
    }

    private List<Path> loadSourceFilePaths() {
        Path sourceRoot = Paths.get(System.getProperty("user.dir"), "src", "main", "java");
        if (!Files.exists(sourceRoot)) {
            log.warn("Source directory not found at {}; answering without code context", sourceRoot);
            return Collections.emptyList();
        }
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            return paths
                    .filter(p -> p.toString().endsWith(".java"))
                    .sorted()
                    .limit(MAX_CONTEXT_FILES)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error scanning source files", e);
            return Collections.emptyList();
        }
    }

    private String buildContext(List<Path> files) {
        StringBuilder context = new StringBuilder();
        for (Path file : files) {
            if (context.length() >= MAX_CONTEXT_CHARS) break;
            try {
                String content = Files.readString(file);
                int remaining = MAX_CONTEXT_CHARS - context.length();
                if (content.length() > remaining) {
                    content = content.substring(0, remaining) + "\n// [truncated]";
                }
                context.append("=== ").append(file.getFileName()).append(" ===\n")
                       .append(content).append("\n\n");
            } catch (IOException e) {
                log.warn("Could not read file: {}", file, e);
            }
        }
        return context.toString();
    }
}
