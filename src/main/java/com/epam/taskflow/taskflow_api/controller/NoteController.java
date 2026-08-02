package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.NoteRequestDTO;
import com.epam.taskflow.taskflow_api.dto.NoteResponseDTO;
import com.epam.taskflow.taskflow_api.service.NoteService;
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
@RequestMapping("/api/tasks/{taskId}/notes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Note Management", description = "APIs for managing notes on a task")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @Operation(summary = "Add a note to a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<NoteResponseDTO> createNote(@PathVariable Long taskId,
                                                      @RequestBody @Valid NoteRequestDTO requestDTO) {
        log.info("POST /api/tasks/{}/notes - Creating note", taskId);
        NoteResponseDTO createdNote = noteService.createNote(taskId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @GetMapping
    @Operation(summary = "Get all notes for a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<List<NoteResponseDTO>> getNotesByTaskId(@PathVariable Long taskId) {
        log.info("GET /api/tasks/{}/notes - Retrieving notes", taskId);
        List<NoteResponseDTO> notes = noteService.getNotesByTaskId(taskId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{noteId}")
    @Operation(summary = "Get a specific note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long taskId,
                                                       @PathVariable Long noteId) {
        log.info("GET /api/tasks/{}/notes/{} - Retrieving note", taskId, noteId);
        NoteResponseDTO note = noteService.getNoteById(noteId);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/{noteId}")
    @Operation(summary = "Update a note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long taskId,
                                                      @PathVariable Long noteId,
                                                      @RequestBody @Valid NoteRequestDTO requestDTO) {
        log.info("PUT /api/tasks/{}/notes/{} - Updating note", taskId, noteId);
        NoteResponseDTO updatedNote = noteService.updateNote(noteId, requestDTO);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{noteId}")
    @Operation(summary = "Delete a note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<Void> deleteNote(@PathVariable Long taskId,
                                           @PathVariable Long noteId) {
        log.info("DELETE /api/tasks/{}/notes/{} - Deleting note", taskId, noteId);
        noteService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}
