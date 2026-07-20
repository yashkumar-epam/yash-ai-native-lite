package com.epam.taskflow.taskflow_api.mapper;

import com.epam.taskflow.taskflow_api.dto.TaskRequestDTO;
import com.epam.taskflow.taskflow_api.dto.TaskResponseDTO;
import com.epam.taskflow.taskflow_api.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskMapper {

    public TaskResponseDTO toResponseDTO(Task task) {
        if (task == null) {
            log.warn("Cannot map Task to TaskResponseDTO: task is null");
            return null;
        }

        log.debug("Mapping Task entity to TaskResponseDTO for id={}", task.getId());
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public Task toEntity(TaskRequestDTO requestDTO) {
        if (requestDTO == null) {
            log.warn("Cannot map TaskRequestDTO to Task: requestDTO is null");
            return null;
        }

        log.debug("Mapping TaskRequestDTO to Task entity");
        return Task.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .status(requestDTO.getStatus())
                .priority(requestDTO.getPriority())
                .build();
    }

    public Task updateEntityFromDTO(TaskRequestDTO requestDTO, Task task) {
        if (requestDTO == null) {
            log.warn("Cannot update Task from TaskRequestDTO: requestDTO is null");
            return task;
        }
        if (task == null) {
            log.warn("Cannot update Task from TaskRequestDTO: task is null");
            return null;
        }

        log.debug("Updating Task entity fields from TaskRequestDTO for id={}", task.getId());

        if (requestDTO.getTitle() != null) {
            task.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getDescription() != null) {
            task.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getStatus() != null) {
            task.setStatus(requestDTO.getStatus());
        }
        if (requestDTO.getPriority() != null) {
            task.setPriority(requestDTO.getPriority());
        }

        return task;
    }
}

