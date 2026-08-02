package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.exception.AiParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class AiResponseParser {

    private static final Pattern FENCE_PATTERN =
            Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)\\s*```");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T parse(String rawResponse, Class<T> targetType) {
        String json = extractJson(rawResponse);
        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response as {}: {}", targetType.getSimpleName(), json);
            throw new AiParsingException(e.getMessage());
        }
    }

    public <T> T parse(String rawResponse, TypeReference<T> typeRef) {
        String json = extractJson(rawResponse);
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response: {}", json);
            throw new AiParsingException(e.getMessage());
        }
    }

    private String extractJson(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new AiParsingException("AI returned an empty response");
        }
        Matcher matcher = FENCE_PATTERN.matcher(raw);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        int start = -1;
        char open = 0;
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '{' || c == '[') {
                start = i;
                open = c;
                break;
            }
        }
        if (start == -1) {
            throw new AiParsingException("No JSON object found in AI response");
        }
        char close = open == '{' ? '}' : ']';
        int end = raw.lastIndexOf(close);
        if (end <= start) {
            throw new AiParsingException("Malformed JSON in AI response");
        }
        return raw.substring(start, end + 1).trim();
    }
}
