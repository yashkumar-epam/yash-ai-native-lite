---
name: explain-codebase
description: This skill should be used when a user asks "how does this project work", "explain the architecture", "walk me through the code", "I'm new to this project", "what is TaskFlow", or when onboarding a new developer. Provides a structured, top-down walkthrough of the TaskFlow codebase.
version: 1.0.0
---

# Explain Codebase

Provides a clear, top-down walkthrough of the TaskFlow API for new developers or stakeholders.

## When This Skill Activates

- User is new to the project and asks how it works
- Someone asks "walk me through the architecture"
- Preparing for a demo or code walkthrough session
- Management asks "what did the AI actually build?"

## Explanation Structure

### 1 — What is TaskFlow?

TaskFlow is a RESTful task-management API built with Spring Boot 3.x.
It manages Tasks with fields: `title`, `description`, `status`, `priority`, `dueDate`, `createdAt`, `updatedAt`.

It is also an **AI-native development showcase** — every feature was built using
Claude Code, GitHub Copilot, and the GitHub MCP server.

---

### 2 — Request Flow (trace a single POST /api/tasks)

```
HTTP POST /api/tasks
  │
  ▼
TaskController.createTask(@Valid @RequestBody TaskRequestDTO)
  │   ← @Valid triggers Jakarta validation (title, status, priority, dueDate)
  │   ← If invalid → GlobalExceptionHandler → 400 JSON error response
  ▼
TaskService.createTask(TaskRequestDTO)
  │   ← @Transactional ensures DB write is atomic
  ▼
TaskMapper.toEntity(TaskRequestDTO)
  │   ← converts DTO → JPA entity (entity never exposed outside service layer)
  ▼
TaskRepository.save(Task)
  │   ← Spring Data JPA → H2 in-memory DB
  ▼
TaskMapper.toResponseDTO(Task)
  │   ← converts entity → response DTO
  ▼
ResponseEntity.status(201).body(TaskResponseDTO)
  │
  ▼
HTTP 201 Created  { id, title, status, priority, dueDate, createdAt, updatedAt }
```

---

### 3 — Key Classes

| Class | Role |
|---|---|
| `Task` | JPA entity — the DB row. Never returned from API directly. |
| `TaskRequestDTO` | What the caller sends. Has `@Valid` constraints. |
| `TaskResponseDTO` | What the API returns. Clean, no JPA annotations. |
| `TaskMapper` | Converts between entity and DTOs. Three methods: `toResponseDTO`, `toEntity`, `updateEntityFromDTO`. |
| `TaskService` | All business logic. `@Transactional` on writes. |
| `TaskController` | HTTP surface. Thin — calls service, wraps in `ResponseEntity`. |
| `TaskRepository` | JPA queries. `findByStatus`, `findByPriority`, `findByDueDateBefore`, etc. |
| `GlobalExceptionHandler` | Catches all exceptions. Returns structured `ErrorResponse` JSON. |

---

### 4 — Validation Chain

1. `@Valid` on the controller parameter triggers Jakarta validation
2. `MethodArgumentNotValidException` is thrown for any constraint violation
3. `GlobalExceptionHandler` catches it, joins all field errors with `"; "`, returns 400

Validation rules (from `TaskRequestDTO`):
- `title`: `@NotBlank` + `@Size(min=3, max=255)`
- `description`: `@Size(max=500)` (optional)
- `status`: `@Pattern(regexp="^(TODO|IN_PROGRESS|DONE)$")`
- `priority`: `@Pattern(regexp="^(LOW|MEDIUM|HIGH)$")`
- `dueDate`: `@FutureOrPresent` (today or future only)

---

### 5 — AI Tools That Built This

| Tool | What it did |
|---|---|
| **Claude Code** | Wrote all source files, tests, and made all git commits |
| **GitHub Copilot** | In-IDE autocomplete; reads `.github/copilot-instructions.md` for conventions |
| **GitHub MCP server** | Gave Claude Code live access to GitHub issues; Day 4 feature was implemented directly from issue #3 text read via MCP |
| **Multi-agent workflows** | Parallel agents review code, generate tests, and implement features across layers simultaneously |
| **Claude Code hooks** | Auto-run `mvn compile` after file edits and `mvn test` before commits |

---

### 6 — Running It

```bash
mvn spring-boot:run          # starts on :8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 console: http://localhost:8080/h2-console

mvn test                     # run all tests
```

---

### 7 — AI-Native Development Journey

Day 1–3: Scaffold + CRUD + validation (Claude Code)
Day 4: GitHub MCP reads issue #3 → implements `dueDate` field → commits → PR opened
Day 5+: More features driven by GitHub issues via MCP
Day 6: RAG over codebase (this README feeds the vector store)
Day 7: End-to-end management showcase
