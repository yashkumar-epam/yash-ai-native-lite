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
            @Value("${dial.api-key}") String dialApiKey) {
        return RestClient.builder()
                .baseUrl(dialEndpoint)
                .defaultHeader("Api-Key", dialApiKey)
                .build();
    }
}
