package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.AiQueryRequestDTO;
import com.epam.taskflow.taskflow_api.dto.AiQueryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiService Unit Tests")
class AiServiceTest {

    @Mock
    private RestClient dialRestClient;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        aiService = new AiService(dialRestClient, "gpt-5-mini-2025-08-07", "2025-04-01-preview");
    }

    @Test
    @DisplayName("askQuestion should return DTO with DIAL model name")
    void askQuestion_shouldReturnDTO_withModelName() {
        AiService spy = spy(aiService);
        doReturn("TaskFlow is a task management REST API.").when(spy).queryModel(anyString(), anyString());

        AiQueryResponseDTO result = spy.askQuestion(new AiQueryRequestDTO("What is TaskFlow?"));

        assertNotNull(result);
        assertEquals("TaskFlow is a task management REST API.", result.getAnswer());
        assertEquals("gpt-5-mini-2025-08-07", result.getModel());
    }

    @Test
    @DisplayName("askQuestion should return non-negative contextFilesUsed")
    void askQuestion_shouldReturnNonNegativeContextFilesUsed() {
        AiService spy = spy(aiService);
        doReturn("Answer").when(spy).queryModel(anyString(), anyString());

        AiQueryResponseDTO result = spy.askQuestion(new AiQueryRequestDTO("How many controllers?"));

        assertTrue(result.getContextFilesUsed() >= 0);
    }

    @Test
    @DisplayName("askQuestion should delegate question to queryModel")
    void askQuestion_shouldPassQuestionToQueryModel() {
        AiService spy = spy(aiService);
        doReturn("42").when(spy).queryModel(anyString(), anyString());

        spy.askQuestion(new AiQueryRequestDTO("How many endpoints?"));

        verify(spy).queryModel(anyString(), anyString());
    }

    @Test
    @DisplayName("askQuestion should include all response fields")
    void askQuestion_shouldIncludeAllResponseFields() {
        AiService spy = spy(aiService);
        doReturn("The source has multiple layers.").when(spy).queryModel(anyString(), anyString());

        AiQueryResponseDTO result = spy.askQuestion(new AiQueryRequestDTO("Describe the layers"));

        assertNotNull(result.getAnswer());
        assertNotNull(result.getModel());
        assertTrue(result.getContextFilesUsed() >= 0);
    }

    @Test
    @DisplayName("askQuestion should return empty answer when queryModel returns empty string")
    void askQuestion_shouldReturnEmptyAnswer_whenQueryModelReturnsEmpty() {
        AiService spy = spy(aiService);
        doReturn("").when(spy).queryModel(anyString(), anyString());

        AiQueryResponseDTO result = spy.askQuestion(new AiQueryRequestDTO("What?"));

        assertEquals("", result.getAnswer());
        assertEquals("gpt-5-mini-2025-08-07", result.getModel());
    }
}
