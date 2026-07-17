package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.model.Task;
import com.epam.taskflow.taskflow_api.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    /**
     * Get all tasks.
     *
     * @return ResponseEntity with list of all tasks and 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        log.info("GET /api/tasks - Retrieving all tasks");
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get a task by id.
     *
     * @param id the task id
     * @return ResponseEntity with the task and 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        log.info("GET /api/tasks/{} - Retrieving task", id);
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Create a new task.
     *
     * @param task the task to create
     * @return ResponseEntity with the created task and 201 CREATED status
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid Task task) {
        log.info("POST /api/tasks - Creating new task with title: {}", task.getTitle());
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * Update an existing task.
     *
     * @param id the task id
     * @param taskDetails the task details to update
     * @return ResponseEntity with the updated task and 200 OK status
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody @Valid Task taskDetails) {
        log.info("PUT /api/tasks/{} - Updating task", id);
        Task updatedTask = taskService.updateTask(id, taskDetails);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Delete a task by id.
     *
     * @param id the task id
     * @return ResponseEntity with 204 NO CONTENT status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /api/tasks/{} - Deleting task", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get tasks by status.
     *
     * @param status the task status
     * @return ResponseEntity with list of tasks and 200 OK status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        log.info("GET /api/tasks/status/{} - Retrieving tasks by status", status);
        List<Task> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by priority.
     *
     * @param priority the task priority
     * @return ResponseEntity with list of tasks and 200 OK status
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable String priority) {
        log.info("GET /api/tasks/priority/{} - Retrieving tasks by priority", priority);
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Search tasks by title keyword.
     *
     * @param keyword the keyword to search for in task titles
     * @return ResponseEntity with list of matching tasks and 200 OK status
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasksByTitle(@RequestParam String keyword) {
        log.info("GET /api/tasks/search?keyword={} - Searching tasks by title", keyword);
        List<Task> tasks = taskService.searchTasksByTitle(keyword);
        return ResponseEntity.ok(tasks);
    }
}

