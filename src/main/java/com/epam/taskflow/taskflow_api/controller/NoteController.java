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
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Note Management", description = "APIs for managing notes")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @Operation(summary = "Create a new note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NoteResponseDTO> createNote(@RequestBody @Valid NoteRequestDTO requestDTO) {
        log.info("POST /api/notes - Creating note for taskId: {}", requestDTO.getTaskId());
        NoteResponseDTO createdNote = noteService.createNote(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id) {
        log.info("GET /api/notes/{} - Retrieving note", id);
        NoteResponseDTO note = noteService.getNoteById(id);
        return ResponseEntity.ok(note);
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get all notes for a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NoteResponseDTO>> getNotesByTaskId(@PathVariable Long taskId) {
        log.info("GET /api/notes/task/{} - Retrieving notes for task", taskId);
        List<NoteResponseDTO> notes = noteService.getNotesByTaskId(taskId);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long id, @RequestBody @Valid NoteRequestDTO requestDTO) {
        log.info("PUT /api/notes/{} - Updating note", id);
        NoteResponseDTO updatedNote = noteService.updateNote(id, requestDTO);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.info("DELETE /api/notes/{} - Deleting note", id);
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}
