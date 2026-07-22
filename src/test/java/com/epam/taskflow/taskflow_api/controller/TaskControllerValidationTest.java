package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.TaskRequestDTO;
import com.epam.taskflow.taskflow_api.dto.TaskResponseDTO;
import com.epam.taskflow.taskflow_api.exception.GlobalExceptionHandler;
import com.epam.taskflow.taskflow_api.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerValidationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        TaskController taskController = new TaskController(taskService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createTask_shouldReturn400_whenTitleIsBlank() throws Exception {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title(" ")
                .description("Valid description")
                .status("TODO")
                .priority("LOW")
                .build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("title: Title cannot be blank")))
                .andExpect(jsonPath("$.path").value("/api/tasks"));
    }

    @Test
    void createTask_shouldReturn400_whenTitleSizeIsInvalid() throws Exception {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("ab")
                .description("Valid description")
                .status("TODO")
                .priority("LOW")
                .build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("title: Title must be between 3 and 100 characters")))
                .andExpect(jsonPath("$.path").value("/api/tasks"));
    }

    @Test
    void createTask_shouldReturn400_whenDescriptionExceedsMaxSize() throws Exception {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("a".repeat(501))
                .status("TODO")
                .priority("LOW")
                .build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("description: Description must not exceed 500 characters")))
                .andExpect(jsonPath("$.path").value("/api/tasks"));
    }

    @Test
    void createTask_shouldReturn400_whenStatusPatternIsInvalid() throws Exception {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Valid description")
                .status("OPEN")
                .priority("LOW")
                .build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("status: Status must be one of: TODO, IN_PROGRESS, DONE")))
                .andExpect(jsonPath("$.path").value("/api/tasks"));
    }

    @Test
    void createTask_shouldReturn400_whenPriorityPatternIsInvalid() throws Exception {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Valid description")
                .status("TODO")
                .priority("URGENT")
                .build();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("priority: Priority must be one of: LOW, MEDIUM, HIGH")))
                .andExpect(jsonPath("$.path").value("/api/tasks"));
    }

    @Test
    void updateTask_shouldReturn400_whenTitleIsBlank() throws Exception {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("")
                .description("Valid description")
                .status("TODO")
                .priority("LOW")
                .build();

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("title: Title cannot be blank")))
                .andExpect(jsonPath("$.path").value("/api/tasks/1"));
    }

    @Test
    void createTask_shouldReturn201_whenRequestIsValid() throws Exception {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Valid description")
                .status("TODO")
                .priority("MEDIUM")
                .build();

        TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Valid Title")
                .description("Valid description")
                .status("TODO")
                .priority("MEDIUM")
                .build();

        when(taskService.createTask(any(TaskRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Valid Title"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateTask_shouldReturn200_whenRequestIsValid() throws Exception {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("Updated Title")
                .description("Updated description")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .build();

        TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated description")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .build();

        when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }
}

