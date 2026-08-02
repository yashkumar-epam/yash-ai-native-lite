package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.AiQueryRequestDTO;
import com.epam.taskflow.taskflow_api.dto.AiQueryResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private final RestClient dialRestClient;
    private final String dialModel;
    private final String dialApiVersion;

    public AiService(RestClient dialRestClient,
                     @Value("${dial.model}") String dialModel,
                     @Value("${dial.api-version}") String dialApiVersion) {
        this.dialRestClient = dialRestClient;
        this.dialModel = dialModel;
        this.dialApiVersion = dialApiVersion;
    }

    public AiQueryResponseDTO askQuestion(AiQueryRequestDTO request) {
        log.info("Processing AI question: {}", request.getQuestion());
        List<Path> sourceFiles = loadSourceFilePaths();
        String context = buildContext(sourceFiles);
        String answer = queryModel(request.getQuestion(), context);
        return AiQueryResponseDTO.builder()
                .answer(answer)
                .model(dialModel)
                .contextFilesUsed(sourceFiles.size())
                .build();
    }

    @SuppressWarnings("unchecked")
    String queryModel(String question, String context) {
        String userMessage = context.isBlank()
                ? question
                : "Context from the codebase:\n\n" + context + "\nQuestion: " + question;

        Map<String, Object> body = Map.of(
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userMessage)
                ),
                "max_completion_tokens", 2048
        );

        Map<String, Object> response = dialRestClient.post()
                .uri("/openai/deployments/{model}/chat/completions?api-version={version}",
                        dialModel, dialApiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
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
