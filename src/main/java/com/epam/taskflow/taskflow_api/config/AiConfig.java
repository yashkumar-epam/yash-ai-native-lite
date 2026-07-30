package com.epam.taskflow.taskflow_api.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AiConfig {

    @Value("${ANTHROPIC_API_KEY:}")
    private String apiKey;

    @Bean
    public AnthropicClient anthropicClient() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("ANTHROPIC_API_KEY is not set — AI features will return an error when called");
            return AnthropicOkHttpClient.builder()
                    .apiKey("not-configured")
                    .build();
        }
        return AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }
}
