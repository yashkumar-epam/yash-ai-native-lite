package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.AiQueryRequestDTO;
import com.epam.taskflow.taskflow_api.dto.AiQueryResponseDTO;
import com.epam.taskflow.taskflow_api.exception.GlobalExceptionHandler;
import com.epam.taskflow.taskflow_api.service.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiController Input Validation Tests")
class AiControllerValidationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AiService aiService;

    @InjectMocks
    private AiController aiController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(aiController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should return 400 when question is blank")
    void ask_shouldReturnBadRequest_whenQuestionIsBlank() throws Exception {
        AiQueryRequestDTO request = new AiQueryRequestDTO("   ");

        mockMvc.perform(post("/api/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Question is required")));
    }

    @Test
    @DisplayName("Should return 400 when question is null")
    void ask_shouldReturnBadRequest_whenQuestionIsNull() throws Exception {
        AiQueryRequestDTO request = new AiQueryRequestDTO(null);

        mockMvc.perform(post("/api/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Question is required")));
    }

    @Test
    @DisplayName("Should return 400 when question is empty")
    void ask_shouldReturnBadRequest_whenQuestionIsEmpty() throws Exception {
        AiQueryRequestDTO request = new AiQueryRequestDTO("");

        mockMvc.perform(post("/api/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Question is required")));
    }

    @Test
    @DisplayName("Should return 400 when question exceeds 1000 characters")
    void ask_shouldReturnBadRequest_whenQuestionExceeds1000Characters() throws Exception {
        AiQueryRequestDTO request = new AiQueryRequestDTO("a".repeat(1001));

        mockMvc.perform(post("/api/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Question cannot exceed 1000 characters")));
    }

    @Test
    @DisplayName("Should return 200 when question is valid")
    void ask_shouldReturnOk_whenQuestionIsValid() throws Exception {
        when(aiService.askQuestion(any())).thenReturn(
                AiQueryResponseDTO.builder()
                        .answer("TaskFlow is a Spring Boot task management API.")
                        .model("claude-opus-4-8")
                        .contextFilesUsed(10)
                        .build()
        );

        AiQueryRequestDTO request = new AiQueryRequestDTO("What is TaskFlow?");

        mockMvc.perform(post("/api/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("TaskFlow is a Spring Boot task management API."))
                .andExpect(jsonPath("$.model").value("claude-opus-4-8"))
                .andExpect(jsonPath("$.contextFilesUsed").value(10));
    }

    @Test
    @DisplayName("Should accept question with exactly 1000 characters")
    void ask_shouldReturnOk_whenQuestionIsExactly1000Characters() throws Exception {
        when(aiService.askQuestion(any())).thenReturn(
                AiQueryResponseDTO.builder().answer("ok").model("claude-opus-4-8").contextFilesUsed(0).build()
        );

        AiQueryRequestDTO request = new AiQueryRequestDTO("a".repeat(1000));

        mockMvc.perform(post("/api/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
