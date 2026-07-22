package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.PagedResponseDTO;
import com.epam.taskflow.taskflow_api.dto.TaskRequestDTO;
import com.epam.taskflow.taskflow_api.dto.TaskResponseDTO;
import com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.epam.taskflow.taskflow_api.mapper.TaskMapper;
import com.epam.taskflow.taskflow_api.model.Task;
import com.epam.taskflow.taskflow_api.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasks_shouldReturnListOfTaskResponseDTO() {
        Task task = Task.builder()
                .id(1L)
                .title("Task 1")
                .description("Desc")
                .status("TODO")
                .priority("HIGH")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Task 1")
                .description("Desc")
                .status("TODO")
                .priority("HIGH")
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();

        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toResponseDTO(task)).thenReturn(responseDTO);

        List<TaskResponseDTO> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        verify(taskRepository, times(1)).findAll();
        verify(taskMapper, times(1)).toResponseDTO(task);
    }

    @Test
    void getAllTasksPaged_shouldReturnPagedResponseDTO() {
        Task task = Task.builder()
                .id(1L)
                .title("Task 1")
                .status("TODO")
                .priority("HIGH")
                .build();
        TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Task 1")
                .status("TODO")
                .priority("HIGH")
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        when(taskMapper.toResponseDTO(task)).thenReturn(responseDTO);

        PagedResponseDTO result = taskService.getAllTasksPaged(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());
        verify(taskRepository, times(1)).findAll(pageable);
        verify(taskMapper, times(1)).toResponseDTO(task);
    }

    @Test
    void getTaskById_shouldReturnTaskResponseDTO_whenTaskExists() {
        Long taskId = 1L;
        Task task = Task.builder().id(taskId).title("Task 1").build();
        TaskResponseDTO responseDTO = TaskResponseDTO.builder().id(taskId).title("Task 1").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponseDTO(task)).thenReturn(responseDTO);

        TaskResponseDTO result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskMapper, times(1)).toResponseDTO(task);
    }

    @Test
    void getTaskById_shouldThrowResourceNotFoundException_whenTaskNotFound() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.getTaskById(taskId)
        );

        assertEquals("Task not found with id: 1", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskMapper, never()).toResponseDTO(org.mockito.ArgumentMatchers.any(Task.class));
    }

    @Test
    void createTask_shouldSaveAndReturnTaskResponseDTO() {
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("New Task")
                .description("New Desc")
                .status("TODO")
                .priority("MEDIUM")
                .build();

        Task task = Task.builder()
                .title("New Task")
                .description("New Desc")
                .status("TODO")
                .priority("MEDIUM")
                .build();

        Task savedTask = Task.builder()
                .id(1L)
                .title("New Task")
                .description("New Desc")
                .status("TODO")
                .priority("MEDIUM")
                .build();

        TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("New Task")
                .description("New Desc")
                .status("TODO")
                .priority("MEDIUM")
                .build();

        when(taskMapper.toEntity(requestDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toResponseDTO(savedTask)).thenReturn(responseDTO);

        TaskResponseDTO result = taskService.createTask(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Task", result.getTitle());
        verify(taskMapper, times(1)).toEntity(requestDTO);
        verify(taskRepository, times(1)).save(task);
        verify(taskMapper, times(1)).toResponseDTO(savedTask);
    }

    @Test
    void updateTask_shouldUpdateAndReturnTaskResponseDTO_whenTaskExists() {
        Long taskId = 1L;
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("Updated Task")
                .description("Updated Desc")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .build();

        Task existingTask = Task.builder()
                .id(taskId)
                .title("Old Task")
                .description("Old Desc")
                .status("TODO")
                .priority("LOW")
                .build();

        Task updatedTask = Task.builder()
                .id(taskId)
                .title("Updated Task")
                .description("Updated Desc")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .build();

        TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                .id(taskId)
                .title("Updated Task")
                .description("Updated Desc")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);
        when(taskMapper.toResponseDTO(updatedTask)).thenReturn(responseDTO);

        TaskResponseDTO result = taskService.updateTask(taskId, requestDTO);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Updated Task", result.getTitle());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskMapper, times(1)).updateEntityFromDTO(requestDTO, existingTask);
        verify(taskRepository, times(1)).save(existingTask);
        verify(taskMapper, times(1)).toResponseDTO(updatedTask);
    }

    @Test
    void updateTask_shouldThrowResourceNotFoundException_whenTaskNotFound() {
        Long taskId = 1L;
        TaskRequestDTO requestDTO = TaskRequestDTO.builder().title("Updated Task").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.updateTask(taskId, requestDTO)
        );

        assertEquals("Task not found with id: 1", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskMapper, never()).updateEntityFromDTO(org.mockito.ArgumentMatchers.any(TaskRequestDTO.class), org.mockito.ArgumentMatchers.any(Task.class));
        verify(taskRepository, never()).save(org.mockito.ArgumentMatchers.any(Task.class));
    }

    @Test
    void deleteTask_shouldDeleteTask_whenTaskExists() {
        Long taskId = 1L;
        Task existingTask = Task.builder().id(taskId).title("Task to delete").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void deleteTask_shouldThrowResourceNotFoundException_whenTaskNotFound() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.deleteTask(taskId)
        );

        assertEquals("Task not found with id: 1", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(org.mockito.ArgumentMatchers.any(Task.class));
    }

    @Test
    void getTasksByStatusPaged_shouldReturnPagedResponseDTO() {
        Task task = Task.builder()
                .id(1L)
                .title("Task 1")
                .status("TODO")
                .priority("HIGH")
                .build();
        TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Task 1")
                .status("TODO")
                .priority("HIGH")
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        when(taskRepository.findByStatus("TODO", pageable)).thenReturn(taskPage);
        when(taskMapper.toResponseDTO(task)).thenReturn(responseDTO);

        PagedResponseDTO result = taskService.getTasksByStatusPaged("TODO", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Task 1", result.getContent().get(0).getTitle());
        assertEquals(1L, result.getTotalElements());
        verify(taskRepository, times(1)).findByStatus("TODO", pageable);
        verify(taskMapper, times(1)).toResponseDTO(task);
    }

    @Test
    void getTasksByPriorityPaged_shouldReturnPagedResponseDTO() {
        Task task = Task.builder()
                .id(1L)
                .title("Task 1")
                .status("TODO")
                .priority("HIGH")
                .build();
        TaskResponseDTO responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Task 1")
                .status("TODO")
                .priority("HIGH")
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        when(taskRepository.findByPriority("HIGH", pageable)).thenReturn(taskPage);
        when(taskMapper.toResponseDTO(task)).thenReturn(responseDTO);

        PagedResponseDTO result = taskService.getTasksByPriorityPaged("HIGH", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("HIGH", result.getContent().get(0).getPriority());
        assertEquals(1, result.getTotalPages());
        verify(taskRepository, times(1)).findByPriority("HIGH", pageable);
        verify(taskMapper, times(1)).toResponseDTO(task);
    }
}

