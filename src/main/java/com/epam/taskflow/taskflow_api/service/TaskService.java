package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.PagedResponseDTO;
import com.epam.taskflow.taskflow_api.dto.TaskRequestDTO;
import com.epam.taskflow.taskflow_api.dto.TaskResponseDTO;
import com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.epam.taskflow.taskflow_api.mapper.TaskMapper;
import com.epam.taskflow.taskflow_api.model.Task;
import com.epam.taskflow.taskflow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * Get all tasks.
     *
     * @return list of all tasks
     */
    public List<TaskResponseDTO> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponseDTO)
                .toList();
    }

    public PagedResponseDTO getAllTasksPaged(int page, int size) {
        log.info("Fetching paged tasks with page={} and size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findAll(pageable);
        return buildPagedResponse(taskPage);
    }

    /**
     * Get a task by id.
     *
     * @param id the task id
     * @return the task
     * @throws ResourceNotFoundException if the task is not found
     */
    public TaskResponseDTO getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.toResponseDTO(task);
    }

    /**
     * Create a new task.
     *
     * @param requestDTO the task to create
     * @return the created task
     */
    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        log.info("Creating task with title: {}", requestDTO.getTitle());
        Task task = taskMapper.toEntity(requestDTO);
        Task createdTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(createdTask);
    }

    /**
     * Update an existing task.
     *
     * @param id the task id
     * @param requestDTO the task details to update
     * @return the updated task
     * @throws ResourceNotFoundException if the task is not found
     */
    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO) {
        log.info("Updating task with id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        taskMapper.updateEntityFromDTO(requestDTO, task);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(updatedTask);
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
    public List<TaskResponseDTO> getTasksByStatus(String status) {
        log.info("Fetching tasks with status: {}", status);
        return taskRepository.findByStatus(status)
                .stream()
                .map(taskMapper::toResponseDTO)
                .toList();
    }

    public PagedResponseDTO getTasksByStatusPaged(String status, int page, int size) {
        log.info("Fetching paged tasks with status={} page={} and size={}", status, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByStatus(status, pageable);
        return buildPagedResponse(taskPage);
    }

    /**
     * Get tasks by priority.
     *
     * @param priority the task priority
     * @return list of tasks with the specified priority
     */
    public List<TaskResponseDTO> getTasksByPriority(String priority) {
        log.info("Fetching tasks with priority: {}", priority);
        return taskRepository.findByPriority(priority)
                .stream()
                .map(taskMapper::toResponseDTO)
                .toList();
    }

    public PagedResponseDTO getTasksByPriorityPaged(String priority, int page, int size) {
        log.info("Fetching paged tasks with priority={} page={} and size={}", priority, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByPriority(priority, pageable);
        return buildPagedResponse(taskPage);
    }

    /**
     * Search tasks by title keyword.
     *
     * @param keyword the keyword to search for in task titles
     * @return list of tasks matching the keyword
     */
    public List<TaskResponseDTO> searchTasksByTitle(String keyword) {
        log.info("Searching tasks by title keyword: {}", keyword);
        return taskRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(taskMapper::toResponseDTO)
                .toList();
    }

    private PagedResponseDTO buildPagedResponse(Page<Task> taskPage) {
        List<TaskResponseDTO> content = taskPage.getContent()
                .stream()
                .map(taskMapper::toResponseDTO)
                .toList();

        return PagedResponseDTO.builder()
                .content(content)
                .pageNumber(taskPage.getNumber())
                .pageSize(taskPage.getSize())
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .last(taskPage.isLast())
                .build();
    }
}
