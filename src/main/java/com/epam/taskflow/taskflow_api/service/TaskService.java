package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.epam.taskflow.taskflow_api.model.Task;
import com.epam.taskflow.taskflow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Get all tasks.
     *
     * @return list of all tasks
     */
    public List<Task> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll();
    }

    /**
     * Get a task by id.
     *
     * @param id the task id
     * @return the task
     * @throws ResourceNotFoundException if the task is not found
     */
    public Task getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    /**
     * Create a new task.
     *
     * @param task the task to create
     * @return the created task
     */
    @Transactional
    public Task createTask(Task task) {
        log.info("Creating task with title: {}", task.getTitle());
        return taskRepository.save(task);
    }

    /**
     * Update an existing task.
     *
     * @param id the task id
     * @param taskDetails the task details to update
     * @return the updated task
     * @throws ResourceNotFoundException if the task is not found
     */
    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        log.info("Updating task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (taskDetails.getTitle() != null) {
            task.setTitle(taskDetails.getTitle());
        }
        if (taskDetails.getDescription() != null) {
            task.setDescription(taskDetails.getDescription());
        }
        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }
        if (taskDetails.getPriority() != null) {
            task.setPriority(taskDetails.getPriority());
        }

        return taskRepository.save(task);
    }

    /**
     * Delete a task by id.
     *
     * @param id the task id
     * @throws ResourceNotFoundException if the task is not found
     */
    @Transactional
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    /**
     * Get tasks by status.
     *
     * @param status the task status
     * @return list of tasks with the specified status
     */
    public List<Task> getTasksByStatus(String status) {
        log.info("Fetching tasks with status: {}", status);
        return taskRepository.findByStatus(status);
    }

    /**
     * Get tasks by priority.
     *
     * @param priority the task priority
     * @return list of tasks with the specified priority
     */
    public List<Task> getTasksByPriority(String priority) {
        log.info("Fetching tasks with priority: {}", priority);
        return taskRepository.findByPriority(priority);
    }

    /**
     * Search tasks by title keyword.
     *
     * @param keyword the keyword to search for in task titles
     * @return list of tasks matching the keyword
     */
    public List<Task> searchTasksByTitle(String keyword) {
        log.info("Searching tasks by title keyword: {}", keyword);
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }
}

