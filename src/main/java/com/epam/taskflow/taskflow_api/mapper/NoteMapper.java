package com.epam.taskflow.taskflow_api.mapper;

import com.epam.taskflow.taskflow_api.dto.NoteRequestDTO;
import com.epam.taskflow.taskflow_api.dto.NoteResponseDTO;
import com.epam.taskflow.taskflow_api.model.Note;
import com.epam.taskflow.taskflow_api.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoteMapper {

    public NoteResponseDTO toResponseDTO(Note note) {
        if (note == null) {
            log.warn("Cannot map Note to NoteResponseDTO: note is null");
            return null;
        }

        log.debug("Mapping Note entity to NoteResponseDTO for id={}", note.getId());
        return NoteResponseDTO.builder()
                .id(note.getId())
                .taskId(note.getTask().getId())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    public Note toEntity(NoteRequestDTO requestDTO, Task task) {
        if (requestDTO == null) {
            log.warn("Cannot map NoteRequestDTO to Note: requestDTO is null");
            return null;
        }
        if (task == null) {
            log.warn("Cannot map NoteRequestDTO to Note: task is null");
            return null;
        }

        log.debug("Mapping NoteRequestDTO to Note entity for taskId={}", task.getId());
        return Note.builder()
                .task(task)
                .content(requestDTO.getContent())
                .build();
    }

    public Note updateEntityFromDTO(NoteRequestDTO requestDTO, Note note) {
        if (requestDTO == null) {
            log.warn("Cannot update Note from NoteRequestDTO: requestDTO is null");
            return note;
        }
        if (note == null) {
            log.warn("Cannot update Note from NoteRequestDTO: note is null");
            return null;
        }

        log.debug("Updating Note entity fields from NoteRequestDTO for id={}", note.getId());

        if (requestDTO.getContent() != null) {
            note.setContent(requestDTO.getContent());
        }

        return note;
    }
}
