---
name: add-endpoint
description: Use this skill when the user says "add an endpoint", "create a new API route", "I need a GET/POST/PUT/DELETE for", or asks to scaffold a new REST operation. Generates the full vertical slice — controller method, service method, DTOs if needed, and test — following all project conventions.
argument-hint: <HTTP-method> <path> <description>
allowed-tools: [Read, Glob, Grep, Edit, Write]
version: 1.0.0
---

# Add Endpoint

Scaffolds a new REST endpoint as a complete vertical slice:
controller method → service method → DTOs (if new) → test.

## When This Skill Activates

- `/add-endpoint GET /api/tasks/overdue Get all overdue tasks`
- `/add-endpoint POST /api/tasks/{id}/complete Mark a task as completed`
- User says "add an endpoint that...", "I need a route for..."

## Arguments

`$ARGUMENTS` format: `<METHOD> <path> <description>`

Example: `GET /api/tasks/overdue Returns tasks whose dueDate is before today`

## Workflow

### Step 1 — Parse the request

Extract from `$ARGUMENTS`:
- HTTP method (GET / POST / PUT / DELETE / PATCH)
- Path (e.g. `/api/tasks/overdue`)
- Description (what the endpoint does)

### Step 2 — Read existing code

```
Read: src/main/java/com/epam/taskflow/taskflow_api/controller/TaskController.java
Read: src/main/java/com/epam/taskflow/taskflow_api/service/TaskService.java
Read: src/main/java/com/epam/taskflow/taskflow_api/repository/TaskRepository.java
```

Identify: existing imports, existing patterns to follow, whether new DTOs are needed.

### Step 3 — Add repository method (if query-based)

If the endpoint requires a database query not already in `TaskRepository`, add it:

```java
// For overdue tasks example:
Page<Task> findByDueDateBefore(LocalDate date, Pageable pageable);
```

### Step 4 — Add service method

In `TaskService`, add a method following this pattern:

```java
public <ReturnType> <methodName>(<params>) {
    log.info("<description>");
    // repository call
    // map to DTO
    return result;
}
```

Rules:
- `@Transactional` if it writes data
- Return `TaskResponseDTO` or `PagedResponseDTO` (not raw entity)
- `log.info(...)` at the start

### Step 5 — Add controller method

In `TaskController`, add the method following this pattern:

```java
@GetMapping("/overdue")   // or @PostMapping, @PutMapping, etc.
@Operation(summary = "<description>")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "..."),
    @ApiResponse(responseCode = "400", description = "Invalid input")
})
public ResponseEntity<PagedResponseDTO> getOverdueTasks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    log.info("GET /api/tasks/overdue page={} size={}", page, size);
    return ResponseEntity.ok(taskService.getOverdueTasks(page, size));
}
```

HTTP status rules:
- GET → 200 OK
- POST → 201 Created (`ResponseEntity.status(HttpStatus.CREATED)`)
- PUT / PATCH → 200 OK
- DELETE → 204 No Content

### Step 6 — Add a test

Add a test method to the appropriate test class:
- Query/service logic → `TaskServiceTest`
- Validation / HTTP behavior → `TaskControllerValidationTest`

For controller tests use `@MockBean TaskService`.

### Step 7 — Verify no import gaps

Check that every new annotation, type, and util class is imported.
Java files that reference `LocalDate`, `Page`, `Pageable`, `HttpStatus` must
import them explicitly — Spring Boot does not auto-import.
