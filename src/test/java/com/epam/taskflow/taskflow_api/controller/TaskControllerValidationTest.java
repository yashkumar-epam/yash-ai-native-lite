package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.TaskRequestDTO;
import com.epam.taskflow.taskflow_api.dto.TaskResponseDTO;
import com.epam.taskflow.taskflow_api.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController Input Validation Tests")
class TaskControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskRequestDTO validTaskRequest;

    @BeforeEach
    void setUp() {
        validTaskRequest = TaskRequestDTO.builder()
                .title("Valid Task Title")
                .description("A valid task description")
                .status("TODO")
                .priority("HIGH")
                .build();
    }

    // ======================== TITLE VALIDATION TESTS ========================

    @Test
    @DisplayName("Should reject blank title - returns 400 with error message")
    void testCreateTaskWithBlankTitle() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("   ")
                .description("Description")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Title is required")));
    }

    @Test
    @DisplayName("Should reject null title - returns 400 with error message")
    void testCreateTaskWithNullTitle() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title(null)
                .description("Description")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Title is required")));
    }

    @Test
    @DisplayName("Should reject empty title - returns 400 with error message")
    void testCreateTaskWithEmptyTitle() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("")
                .description("Description")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Title is required")));
    }

    @Test
    @DisplayName("Should reject title exceeding 255 characters")
    void testCreateTaskWithTitleExceedingMaxLength() throws Exception {
        String longTitle = "a".repeat(256);
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title(longTitle)
                .description("Description")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("cannot exceed 255 characters")));
    }

    @Test
    @DisplayName("Should accept title with exactly 255 characters")
    void testCreateTaskWithMaxLengthTitle() throws Exception {
        String maxTitle = "a".repeat(255);
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title(maxTitle)
                .description("Description")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ======================== STATUS VALIDATION TESTS ========================

    @Test
    @DisplayName("Should reject invalid status - returns 400 with clear message")
    void testCreateTaskWithInvalidStatus() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("INVALID_STATUS")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Status must be one of: TODO, IN_PROGRESS, DONE")));
    }

    @Test
    @DisplayName("Should accept TODO status")
    void testCreateTaskWithTODOStatus() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept IN_PROGRESS status")
    void testCreateTaskWithInProgressStatus() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept DONE status")
    void testCreateTaskWithDoneStatus() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("DONE")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should reject lowercase status variations")
    void testCreateTaskWithLowercaseStatus() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("todo")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ======================== PRIORITY VALIDATION TESTS ========================

    @Test
    @DisplayName("Should reject invalid priority - returns 400 with clear message")
    void testCreateTaskWithInvalidPriority() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("TODO")
                .priority("URGENT")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Priority must be one of: LOW, MEDIUM, HIGH")));
    }

    @Test
    @DisplayName("Should accept LOW priority")
    void testCreateTaskWithLowPriority() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("TODO")
                .priority("LOW")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept MEDIUM priority")
    void testCreateTaskWithMediumPriority() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("TODO")
                .priority("MEDIUM")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept HIGH priority")
    void testCreateTaskWithHighPriority() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should reject lowercase priority variations")
    void testCreateTaskWithLowercasePriority() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("TODO")
                .priority("low")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ======================== DESCRIPTION VALIDATION TESTS ========================

    @Test
    @DisplayName("Should reject description exceeding 500 characters")
    void testCreateTaskWithDescriptionExceedingMaxLength() throws Exception {
        String longDescription = "a".repeat(501);
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description(longDescription)
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Description cannot exceed 500 characters")));
    }

    @Test
    @DisplayName("Should accept description with exactly 500 characters")
    void testCreateTaskWithMaxLengthDescription() throws Exception {
        String maxDescription = "a".repeat(500);
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description(maxDescription)
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept empty description")
    void testCreateTaskWithEmptyDescription() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept null description")
    void testCreateTaskWithNullDescription() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description(null)
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ======================== MULTIPLE VALIDATION ERRORS TESTS ========================

    @Test
    @DisplayName("Should return all validation errors when multiple fields are invalid")
    void testCreateTaskWithMultipleValidationErrors() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("")
                .description("a".repeat(501))
                .status("INVALID")
                .priority("INVALID")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("title")))
                .andExpect(jsonPath("$.message", containsString("description")))
                .andExpect(jsonPath("$.message", containsString("status")))
                .andExpect(jsonPath("$.message", containsString("priority")));
    }

    // ======================== UPDATE ENDPOINT VALIDATION TESTS ========================

    @Test
    @DisplayName("Should reject blank title in update - returns 400")
    void testUpdateTaskWithBlankTitle() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("   ")
                .description("Description")
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Title is required")));
    }

    @Test
    @DisplayName("Should reject invalid status in update - returns 400")
    void testUpdateTaskWithInvalidStatus() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("COMPLETED")
                .priority("HIGH")
                .build();

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Status must be one of: TODO, IN_PROGRESS, DONE")));
    }

    @Test
    @DisplayName("Should reject invalid priority in update - returns 400")
    void testUpdateTaskWithInvalidPriority() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("Description")
                .status("TODO")
                .priority("CRITICAL")
                .build();

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Priority must be one of: LOW, MEDIUM, HIGH")));
    }

    @Test
    @DisplayName("Should reject oversized description in update - returns 400")
    void testUpdateTaskWithOversizedDescription() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title")
                .description("a".repeat(501))
                .status("TODO")
                .priority("HIGH")
                .build();

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Description cannot exceed 500 characters")));
    }

    // ======================== EDGE CASE TESTS ========================

    @Test
    @DisplayName("Should accept valid request with all fields")
    void testCreateTaskWithAllValidFields() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTaskRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept valid request with minimum required fields")
    void testCreateTaskWithOnlyTitle() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Valid Title Only")
                .build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
