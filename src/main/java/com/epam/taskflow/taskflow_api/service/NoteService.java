package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.NoteRequestDTO;
import com.epam.taskflow.taskflow_api.dto.NoteResponseDTO;
import com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.epam.taskflow.taskflow_api.mapper.NoteMapper;
import com.epam.taskflow.taskflow_api.model.Note;
import com.epam.taskflow.taskflow_api.model.Task;
import com.epam.taskflow.taskflow_api.repository.NoteRepository;
import com.epam.taskflow.taskflow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final TaskRepository taskRepository;

    @Transactional
    public NoteResponseDTO createNote(Long taskId, NoteRequestDTO requestDTO) {
        log.info("Creating note for taskId: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        Note note = noteMapper.toEntity(requestDTO, task);
        Note savedNote = noteRepository.save(note);
        return noteMapper.toResponseDTO(savedNote);
    }

    public NoteResponseDTO getNoteById(Long id) {
        log.info("Fetching note with id: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        return noteMapper.toResponseDTO(note);
    }

    public List<NoteResponseDTO> getNotesByTaskId(Long taskId) {
        log.info("Fetching notes for taskId: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        return noteRepository.findByTask(task)
                .stream()
                .map(noteMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public NoteResponseDTO updateNote(Long id, NoteRequestDTO requestDTO) {
        log.info("Updating note with id: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        noteMapper.updateEntityFromDTO(requestDTO, note);
        Note updatedNote = noteRepository.save(note);
        return noteMapper.toResponseDTO(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id) {
        log.info("Deleting note with id: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        noteRepository.delete(note);
    }
}
