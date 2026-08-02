package com.epam.taskflow.taskflow_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DialGateway {

    private final RestClient dialRestClient;
    private final String model;
    private final String apiVersion;

    public DialGateway(RestClient dialRestClient,
                       @Value("${dial.model}") String model,
                       @Value("${dial.api-version}") String apiVersion) {
        this.dialRestClient = dialRestClient;
        this.model = model;
        this.apiVersion = apiVersion;
    }

    @SuppressWarnings("unchecked")
    public String chat(String systemPrompt, String userMessage) {
        log.debug("Calling DIAL model={}", model);
        Map<String, Object> body = Map.of(
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "max_completion_tokens", 4096
        );

        Map<String, Object> response = dialRestClient.post()
                .uri("/openai/deployments/{model}/chat/completions?api-version={version}",
                        model, apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    public String getModel() {
        return model;
    }
}
