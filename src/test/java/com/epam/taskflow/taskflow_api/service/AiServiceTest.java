package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.AiQueryRequestDTO;
import com.epam.taskflow.taskflow_api.dto.AiQueryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiService Unit Tests")
class AiServiceTest {

    @Mock
    private DialGateway dialGateway;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        aiService = new AiService(dialGateway);
    }

    @Test
    @DisplayName("askQuestion should return DTO with model name from DialGateway")
    void askQuestion_shouldReturnDTO_withModelName() {
        when(dialGateway.chat(anyString(), anyString())).thenReturn("TaskFlow is a task management REST API.");
        when(dialGateway.getModel()).thenReturn("gpt-5-mini-2025-08-07");

        AiQueryResponseDTO result = aiService.askQuestion(new AiQueryRequestDTO("What is TaskFlow?"));

        assertNotNull(result);
        assertEquals("TaskFlow is a task management REST API.", result.getAnswer());
        assertEquals("gpt-5-mini-2025-08-07", result.getModel());
    }

    @Test
    @DisplayName("askQuestion should return non-negative contextFilesUsed")
    void askQuestion_shouldReturnNonNegativeContextFilesUsed() {
        when(dialGateway.chat(anyString(), anyString())).thenReturn("Answer");
        when(dialGateway.getModel()).thenReturn("gpt-5-mini-2025-08-07");

        AiQueryResponseDTO result = aiService.askQuestion(new AiQueryRequestDTO("How many controllers?"));

        assertTrue(result.getContextFilesUsed() >= 0);
    }

    @Test
    @DisplayName("askQuestion should delegate to DialGateway for the model call")
    void askQuestion_shouldDelegateToDialGateway() {
        when(dialGateway.chat(anyString(), anyString())).thenReturn("42");
        when(dialGateway.getModel()).thenReturn("gpt-5-mini-2025-08-07");

        aiService.askQuestion(new AiQueryRequestDTO("How many endpoints?"));

        org.mockito.Mockito.verify(dialGateway).chat(anyString(), anyString());
    }

    @Test
    @DisplayName("askQuestion should include all response fields")
    void askQuestion_shouldIncludeAllResponseFields() {
        when(dialGateway.chat(anyString(), anyString())).thenReturn("The source has multiple layers.");
        when(dialGateway.getModel()).thenReturn("gpt-5-mini-2025-08-07");

        AiQueryResponseDTO result = aiService.askQuestion(new AiQueryRequestDTO("Describe the layers"));

        assertNotNull(result.getAnswer());
        assertNotNull(result.getModel());
        assertTrue(result.getContextFilesUsed() >= 0);
    }

    @Test
    @DisplayName("askQuestion should return empty answer when DialGateway returns empty string")
    void askQuestion_shouldReturnEmptyAnswer_whenDialGatewayReturnsEmpty() {
        when(dialGateway.chat(anyString(), anyString())).thenReturn("");
        when(dialGateway.getModel()).thenReturn("gpt-5-mini-2025-08-07");

        AiQueryResponseDTO result = aiService.askQuestion(new AiQueryRequestDTO("What?"));

        assertEquals("", result.getAnswer());
        assertEquals("gpt-5-mini-2025-08-07", result.getModel());
    }
}
