package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.NoteRequestDTO;
import com.epam.taskflow.taskflow_api.dto.NoteResponseDTO;
import com.epam.taskflow.taskflow_api.exception.GlobalExceptionHandler;
import com.epam.taskflow.taskflow_api.service.NoteService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("NoteController Input Validation Tests")
class NoteControllerValidationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteController noteController;

    private NoteRequestDTO validNoteRequest;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(noteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();

        validNoteRequest = NoteRequestDTO.builder()
                .content("Valid note content")
                .build();
    }

    @Test
    @DisplayName("Should reject blank content - returns 400 with error message")
    void createNote_shouldReturnBadRequest_whenContentIsBlank() throws Exception {
        NoteRequestDTO request = NoteRequestDTO.builder().content("   ").build();

        mockMvc.perform(post("/api/tasks/1/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Content is required")));
    }

    @Test
    @DisplayName("Should reject null content - returns 400 with error message")
    void createNote_shouldReturnBadRequest_whenContentIsNull() throws Exception {
        NoteRequestDTO request = NoteRequestDTO.builder().content(null).build();

        mockMvc.perform(post("/api/tasks/1/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Content is required")));
    }

    @Test
    @DisplayName("Should reject empty content - returns 400 with error message")
    void createNote_shouldReturnBadRequest_whenContentIsEmpty() throws Exception {
        NoteRequestDTO request = NoteRequestDTO.builder().content("").build();

        mockMvc.perform(post("/api/tasks/1/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Content is required")));
    }

    @Test
    @DisplayName("Should reject content exceeding 1000 characters")
    void createNote_shouldReturnBadRequest_whenContentExceeds1000Characters() throws Exception {
        NoteRequestDTO request = NoteRequestDTO.builder().content("a".repeat(1001)).build();

        mockMvc.perform(post("/api/tasks/1/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Content cannot exceed 1000 characters")));
    }

    @Test
    @DisplayName("Should accept content with exactly 1000 characters")
    void createNote_shouldAcceptContent_whenContentIsExactly1000Characters() throws Exception {
        NoteRequestDTO request = NoteRequestDTO.builder().content("a".repeat(1000)).build();

        when(noteService.createNote(eq(1L), any())).thenReturn(
                NoteResponseDTO.builder().id(1L).taskId(1L).content("a".repeat(1000)).build()
        );

        mockMvc.perform(post("/api/tasks/1/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 201 when all fields are valid")
    void createNote_shouldReturnCreated_whenAllFieldsAreValid() throws Exception {
        when(noteService.createNote(eq(1L), any())).thenReturn(
                NoteResponseDTO.builder().id(1L).taskId(1L).content("Valid note content").build()
        );

        mockMvc.perform(post("/api/tasks/1/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validNoteRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should reject blank content in update - returns 400")
    void updateNote_shouldReturnBadRequest_whenContentIsBlank() throws Exception {
        NoteRequestDTO request = NoteRequestDTO.builder().content("   ").build();

        mockMvc.perform(put("/api/tasks/1/notes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Content is required")));
    }

    @Test
    @DisplayName("Should reject content exceeding 1000 characters in update - returns 400")
    void updateNote_shouldReturnBadRequest_whenContentExceeds1000Characters() throws Exception {
        NoteRequestDTO request = NoteRequestDTO.builder().content("a".repeat(1001)).build();

        mockMvc.perform(put("/api/tasks/1/notes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Content cannot exceed 1000 characters")));
    }

    @Test
    @DisplayName("Should return 200 when all fields are valid in update")
    void updateNote_shouldReturnOk_whenAllFieldsAreValid() throws Exception {
        when(noteService.updateNote(any(), any())).thenReturn(
                NoteResponseDTO.builder().id(1L).taskId(1L).content("Updated content").build()
        );

        NoteRequestDTO request = NoteRequestDTO.builder().content("Updated content").build();

        mockMvc.perform(put("/api/tasks/1/notes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
