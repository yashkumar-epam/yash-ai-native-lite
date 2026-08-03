package com.epam.taskflow.taskflow_api.service;

import com.epam.taskflow.taskflow_api.dto.NoteRequestDTO;
import com.epam.taskflow.taskflow_api.dto.NoteResponseDTO;
import com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.epam.taskflow.taskflow_api.mapper.NoteMapper;
import com.epam.taskflow.taskflow_api.model.Note;
import com.epam.taskflow.taskflow_api.model.Task;
import com.epam.taskflow.taskflow_api.repository.NoteRepository;
import com.epam.taskflow.taskflow_api.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void createNote_shouldSaveAndReturnNoteResponseDTO_whenTaskExists() {
        Task task = Task.builder().id(1L).title("Test Task").build();
        Note note = Note.builder().id(1L).task(task).content("Test content").build();
        Note savedNote = Note.builder().id(1L).task(task).content("Test content").build();
        NoteResponseDTO responseDTO = NoteResponseDTO.builder().id(1L).taskId(1L).content("Test content").build();
        NoteRequestDTO requestDTO = NoteRequestDTO.builder().content("Test content").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(noteMapper.toEntity(requestDTO, task)).thenReturn(note);
        when(noteRepository.save(note)).thenReturn(savedNote);
        when(noteMapper.toResponseDTO(savedNote)).thenReturn(responseDTO);

        NoteResponseDTO result = noteService.createNote(1L, requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test content", result.getContent());
        verify(taskRepository, times(1)).findById(1L);
        verify(noteMapper, times(1)).toEntity(requestDTO, task);
        verify(noteRepository, times(1)).save(note);
        verify(noteMapper, times(1)).toResponseDTO(savedNote);
    }

    @Test
    void createNote_shouldThrowResourceNotFoundException_whenTaskNotFound() {
        NoteRequestDTO requestDTO = NoteRequestDTO.builder().content("Test content").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> noteService.createNote(1L, requestDTO)
        );

        assertEquals("Task not found with id: 1", exception.getMessage());
        verify(taskRepository, times(1)).findById(1L);
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void getNoteById_shouldReturnNoteResponseDTO_whenNoteExists() {
        Task task = Task.builder().id(1L).title("Test Task").build();
        Note note = Note.builder().id(1L).task(task).content("Test content").build();
        NoteResponseDTO responseDTO = NoteResponseDTO.builder().id(1L).taskId(1L).content("Test content").build();

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
        when(noteMapper.toResponseDTO(note)).thenReturn(responseDTO);

        NoteResponseDTO result = noteService.getNoteById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(noteRepository, times(1)).findById(1L);
        verify(noteMapper, times(1)).toResponseDTO(note);
    }

    @Test
    void getNoteById_shouldThrowResourceNotFoundException_whenNoteNotFound() {
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> noteService.getNoteById(1L)
        );

        assertEquals("Note not found with id: 1", exception.getMessage());
        verify(noteRepository, times(1)).findById(1L);
        verify(noteMapper, never()).toResponseDTO(any(Note.class));
    }

    @Test
    void getNotesByTaskId_shouldReturnListOfNoteResponseDTO_whenTaskAndNotesExist() {
        Task task = Task.builder().id(1L).title("Test Task").build();
        Note note = Note.builder().id(1L).task(task).content("Test content").build();
        NoteResponseDTO responseDTO = NoteResponseDTO.builder().id(1L).taskId(1L).content("Test content").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(noteRepository.findByTask(task)).thenReturn(List.of(note));
        when(noteMapper.toResponseDTO(note)).thenReturn(responseDTO);

        List<NoteResponseDTO> result = noteService.getNotesByTaskId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test content", result.get(0).getContent());
        verify(taskRepository, times(1)).findById(1L);
        verify(noteRepository, times(1)).findByTask(task);
        verify(noteMapper, times(1)).toResponseDTO(note);
    }

    @Test
    void getNotesByTaskId_shouldReturnEmptyList_whenTaskExistsButHasNoNotes() {
        Task task = Task.builder().id(1L).title("Test Task").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(noteRepository.findByTask(task)).thenReturn(Collections.emptyList());

        List<NoteResponseDTO> result = noteService.getNotesByTaskId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).findById(1L);
        verify(noteRepository, times(1)).findByTask(task);
    }

    @Test
    void getNotesByTaskId_shouldThrowResourceNotFoundException_whenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> noteService.getNotesByTaskId(1L)
        );

        assertEquals("Task not found with id: 1", exception.getMessage());
        verify(taskRepository, times(1)).findById(1L);
        verify(noteRepository, never()).findByTask(any(Task.class));
    }

    @Test
    void updateNote_shouldUpdateAndReturnNoteResponseDTO_whenNoteExists() {
        Task task = Task.builder().id(1L).title("Test Task").build();
        Note note = Note.builder().id(1L).task(task).content("Old content").build();
        Note updatedNote = Note.builder().id(1L).task(task).content("Updated content").build();
        NoteResponseDTO responseDTO = NoteResponseDTO.builder().id(1L).taskId(1L).content("Updated content").build();
        NoteRequestDTO requestDTO = NoteRequestDTO.builder().content("Updated content").build();

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
        when(noteRepository.save(note)).thenReturn(updatedNote);
        when(noteMapper.toResponseDTO(updatedNote)).thenReturn(responseDTO);

        NoteResponseDTO result = noteService.updateNote(1L, requestDTO);

        assertNotNull(result);
        assertEquals("Updated content", result.getContent());
        verify(noteRepository, times(1)).findById(1L);
        verify(noteMapper, times(1)).updateEntityFromDTO(requestDTO, note);
        verify(noteRepository, times(1)).save(note);
        verify(noteMapper, times(1)).toResponseDTO(updatedNote);
    }

    @Test
    void updateNote_shouldThrowResourceNotFoundException_whenNoteNotFound() {
        NoteRequestDTO requestDTO = NoteRequestDTO.builder().content("Updated content").build();

        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> noteService.updateNote(1L, requestDTO)
        );

        assertEquals("Note not found with id: 1", exception.getMessage());
        verify(noteRepository, times(1)).findById(1L);
        verify(noteMapper, never()).updateEntityFromDTO(any(NoteRequestDTO.class), any(Note.class));
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void deleteNote_shouldDeleteNote_whenNoteExists() {
        Task task = Task.builder().id(1L).title("Test Task").build();
        Note note = Note.builder().id(1L).task(task).content("Test content").build();

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        noteService.deleteNote(1L);

        verify(noteRepository, times(1)).findById(1L);
        verify(noteRepository, times(1)).delete(note);
    }

    @Test
    void deleteNote_shouldThrowResourceNotFoundException_whenNoteNotFound() {
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> noteService.deleteNote(1L)
        );

        assertEquals("Note not found with id: 1", exception.getMessage());
        verify(noteRepository, times(1)).findById(1L);
        verify(noteRepository, never()).delete(any(Note.class));
    }
}
