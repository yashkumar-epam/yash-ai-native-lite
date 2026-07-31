package com.epam.taskflow.taskflow_api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class AiConfig {

    @Bean
    public RestClient dialRestClient(
            @Value("${dial.endpoint}") String dialEndpoint,
            @Value("${dial.api-key:not-configured}") String dialApiKey) {
        if ("not-configured".equals(dialApiKey)) {
            log.warn("DIAL_API_KEY is not set — set the DIAL_API_KEY environment variable to enable AI features");
        }
        return RestClient.builder()
                .baseUrl(dialEndpoint)
                .defaultHeader("Api-Key", dialApiKey)
                .build();
    }
}
