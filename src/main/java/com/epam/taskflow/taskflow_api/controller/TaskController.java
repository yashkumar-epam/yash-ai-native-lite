package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.PagedResponseDTO;
import com.epam.taskflow.taskflow_api.dto.TaskRequestDTO;
import com.epam.taskflow.taskflow_api.dto.TaskResponseDTO;
import com.epam.taskflow.taskflow_api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        log.info("GET /api/tasks - Retrieving all tasks");
        List<TaskResponseDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/paged")
    @Operation(summary = "Get paged tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paged tasks retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PagedResponseDTO> getAllTasksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/tasks/paged?page={}&size={} - Retrieving paged tasks", page, size);
        PagedResponseDTO pagedResponse = taskService.getAllTasksPaged(page, size);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        log.info("GET /api/tasks/{} - Retrieving task", id);
        TaskResponseDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody @Valid TaskRequestDTO requestDTO) {
        log.info("POST /api/tasks - Creating new task with title: {}", requestDTO.getTitle());
        TaskResponseDTO createdTask = taskService.createTask(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @RequestBody @Valid TaskRequestDTO requestDTO) {
        log.info("PUT /api/tasks/{} - Updating task", id);
        TaskResponseDTO updatedTask = taskService.updateTask(id, requestDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /api/tasks/{} - Deleting task", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskResponseDTO>> getTasksByStatus(@PathVariable String status) {
        log.info("GET /api/tasks/status/{} - Retrieving tasks by status", status);
        List<TaskResponseDTO> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/paged/status/{status}")
    @Operation(summary = "Get paged tasks by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paged tasks retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PagedResponseDTO> getTasksByStatusPaged(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/tasks/paged/status/{}?page={}&size={} - Retrieving paged tasks by status", status, page, size);
        PagedResponseDTO pagedResponse = taskService.getTasksByStatusPaged(status, page, size);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskResponseDTO>> getTasksByPriority(@PathVariable String priority) {
        log.info("GET /api/tasks/priority/{} - Retrieving tasks by priority", priority);
        List<TaskResponseDTO> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/paged/priority/{priority}")
    @Operation(summary = "Get paged tasks by priority")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paged tasks retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PagedResponseDTO> getTasksByPriorityPaged(
            @PathVariable String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/tasks/paged/priority/{}?page={}&size={} - Retrieving paged tasks by priority", priority, page, size);
        PagedResponseDTO pagedResponse = taskService.getTasksByPriorityPaged(priority, page, size);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks by title keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid keyword"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskResponseDTO>> searchTasksByTitle(@RequestParam String keyword) {
        log.info("GET /api/tasks/search?keyword={} - Searching tasks by title", keyword);
        List<TaskResponseDTO> tasks = taskService.searchTasksByTitle(keyword);
        return ResponseEntity.ok(tasks);
    }
}
