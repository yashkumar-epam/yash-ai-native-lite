# TaskFlow API

![CI](https://github.com/yashkumar-epam/yash-ai-native-lite/actions/workflows/ci.yml/badge.svg)

A production-grade Spring Boot 3.x REST API for task management, built as an **AI-native development showcase**. The project demonstrates how modern AI tools — Claude Code, GitHub Copilot, and the GitHub MCP server — can compress a full software development lifecycle from requirements to production-ready code.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [AI Tools & Skills Used](#ai-tools--skills-used)
- [Architecture](#architecture)
- [API Reference](#api-reference)
- [Getting Started](#getting-started)
- [Development Journey (AI-Native Days)](#development-journey-ai-native-days)
- [Real Use Case for Management Showcase](#real-use-case-for-management-showcase)

---

## Project Overview

TaskFlow is a RESTful task-management API with:

- Full CRUD for tasks (create, read, update, delete)
- Task notes sub-resource (CRUD notes per task)
- Field validation (status, priority, due-date, size limits)
- Pagination and filtering by status, priority, and keyword
- Structured error responses (RFC-style `ErrorResponse`)
- OpenAPI / Swagger UI documentation
- Due-date tracking with future-date enforcement
- RAG-powered AI endpoint (`POST /api/ai/ask`) that answers questions about the codebase using the EPAM DIAL AI Proxy

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 21, Spring Boot 3.x |
| Persistence | Spring Data JPA, Hibernate, H2 (in-memory) |
| Validation | Jakarta Validation (`@Valid`, `@FutureOrPresent`, `@Pattern`, `@Size`) |
| Boilerplate | Lombok (`@Data`, `@Builder`, `@Slf4j`, `@RequiredArgsConstructor`) |
| API Docs | SpringDoc OpenAPI 3 / Swagger UI |
| Testing | JUnit 5, Mockito, Spring Boot Test (`@WebMvcTest`, `@MockBean`) |
| AI Proxy | EPAM DIAL AI Proxy (OpenAI-compatible) — `gpt-5-mini-2025-08-07` |
| Build | Maven |

---

## AI Tools & Skills Used

### 1. Claude Code (Primary AI Assistant)

**What it is:** Anthropic's official CLI for Claude — an AI pair programmer that reads and writes code, runs tools, manages git, and calls external services.

**How it was used:**
- Generated the full Spring Boot project skeleton (Days 1–2)
- Wrote all service, controller, mapper, repository, and exception-handler classes
- Created comprehensive unit and integration tests
- Performed multi-file refactors with full codebase awareness
- Committed code and opened pull requests

**Configuration:** `.claude/settings.json` (excluded from git — contains tokens)

**Skill invoked:** `claude-api` — loads Anthropic SDK patterns and model defaults for building LLM-powered applications

---

### 2. GitHub Copilot (In-IDE AI Suggestions)

**What it is:** GitHub's AI code-completion tool, integrated into VS Code and JetBrains IDEs.

**How it was used:**
- In-editor autocomplete for boilerplate (Lombok builders, Swagger annotations)
- Standing conventions loaded via `.github/copilot-instructions.md` so every suggestion respects the project's architecture rules

**Configuration:** `.github/copilot-instructions.md`

---

### 3. GitHub MCP Server (Issue-Driven Development)

**What it is:** The `@modelcontextprotocol/server-github` MCP server, which gives Claude Code direct, authenticated access to the GitHub API — issues, PRs, commits, repositories — as native tools.

**How it was used (Day 4):**
1. GitHub issue [#3 — Add due-date field to Task](https://github.com/yashkumar-epam/yash-ai-native-lite/issues/3) was created with full requirements and acceptance criteria
2. A Claude Code agent read the issue back **via the MCP server** (not by copying text)
3. The agent implemented every requirement in the issue: entity field, DTO validation, mapper, repository method, and tests
4. A commit referencing `closes #3` and a PR were created automatically

**Configuration (VS Code):** `.vscode/mcp.json`

```json
{
  "servers": {
    "github": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": "<your-PAT>"
      }
    }
  }
}
```

**Configuration (Claude Code CLI):** `.claude/settings.json` (same structure, excluded from git)

---

### 4. Model Context Protocol (MCP)

**What it is:** An open standard by Anthropic that lets AI agents connect to external tools and data sources (GitHub, databases, filesystems, APIs) using a standardised protocol.

**Why it matters:** MCP is the bridge that makes Day 4's "AI reads ticket → writes code" flow possible. Without MCP, the AI would need the developer to copy-paste the issue text. With MCP, the AI calls GitHub directly as a tool — the same way a developer would open a browser tab.

---

## Architecture

```
com.epam.taskflow.taskflow_api
├── config/
│   ├── AiConfig.java               # RestClient bean for EPAM DIAL AI Proxy
│   └── OpenApiConfig.java          # Swagger / OpenAPI 3 setup
├── controller/
│   ├── AiController.java           # POST /api/ai/ask — RAG Q&A endpoint
│   ├── NoteController.java         # REST endpoints (/api/tasks/{id}/notes/**)
│   └── TaskController.java         # REST endpoints (/api/tasks/**)
├── dto/
│   ├── AiQueryRequestDTO.java      # Validated AI question payload
│   ├── AiQueryResponseDTO.java     # AI answer + model + context file count
│   ├── NoteRequestDTO.java         # Validated note inbound payload
│   ├── NoteResponseDTO.java        # Outbound note shape
│   ├── PagedResponseDTO.java       # Pagination wrapper
│   ├── TaskRequestDTO.java         # Validated inbound payload
│   └── TaskResponseDTO.java        # Outbound shape (never exposes entity)
├── exception/
│   ├── GlobalExceptionHandler.java # @RestControllerAdvice — all errors here
│   └── ResourceNotFoundException.java
├── mapper/
│   ├── NoteMapper.java             # Note entity ↔ DTO conversion
│   └── TaskMapper.java             # Task entity ↔ DTO conversion
├── model/
│   ├── Note.java                   # JPA entity (belongs to Task, cascade delete)
│   └── Task.java                   # JPA entity
├── repository/
│   ├── NoteRepository.java         # Spring Data JPA for notes
│   └── TaskRepository.java         # Spring Data JPA + custom query methods
└── service/
    ├── AiService.java              # RAG: loads source files, calls DIAL proxy
    ├── NoteService.java            # Note business logic, @Transactional writes
    └── TaskService.java            # Task business logic, @Transactional writes
```

**Design principles enforced via `.github/copilot-instructions.md`:**
- Constructor injection only (no `@Autowired`)
- DTOs everywhere — entities never leave the service layer
- All writes are `@Transactional`
- All errors through `GlobalExceptionHandler`
- `@Slf4j` on every class
- Feature-first package structure

---

## API Reference

Base URL: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

**Tasks**

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/tasks` | List all tasks |
| `GET` | `/api/tasks/paged` | Paginated task list |
| `GET` | `/api/tasks/{id}` | Get task by ID |
| `POST` | `/api/tasks` | Create task |
| `PUT` | `/api/tasks/{id}` | Update task |
| `DELETE` | `/api/tasks/{id}` | Delete task (204) |
| `GET` | `/api/tasks/status/{status}` | Filter by status |
| `GET` | `/api/tasks/priority/{priority}` | Filter by priority |
| `GET` | `/api/tasks/search?keyword=` | Search by title |

**Notes** (sub-resource of Task)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/tasks/{taskId}/notes` | List all notes for a task |
| `POST` | `/api/tasks/{taskId}/notes` | Add a note to a task |
| `GET` | `/api/tasks/{taskId}/notes/{noteId}` | Get a specific note |
| `PUT` | `/api/tasks/{taskId}/notes/{noteId}` | Update a note |
| `DELETE` | `/api/tasks/{taskId}/notes/{noteId}` | Delete a note (204) |

### Note payload

```json
{ "content": "string (required, max 1000 chars)" }
```

> `taskId` comes from the URL path — do not include it in the request body.

**AI**

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/ai/ask` | Ask a question about the codebase (RAG) |

### AI request/response

```json
// Request
{ "question": "What does the TaskController do?" }

// Response
{
  "answer": "...",
  "model": "gpt-5-mini-2025-08-07",
  "contextFilesUsed": 20
}
```

### Task payload

```json
{
  "title":       "string (required, 3–255 chars)",
  "description": "string (optional, max 500 chars)",
  "status":      "TODO | IN_PROGRESS | DONE",
  "priority":    "LOW | MEDIUM | HIGH",
  "dueDate":     "YYYY-MM-DD (optional, today or future)"
}
```

### Validation rules

| Field | Rule | Error |
|---|---|---|
| `title` | Required, 3–255 chars | 400 `"Title is required"` / `"Title cannot exceed 255 characters"` |
| `description` | Max 500 chars | 400 `"Description cannot exceed 500 characters"` |
| `status` | Enum: `TODO`, `IN_PROGRESS`, `DONE` | 400 `"Status must be one of: ..."` |
| `priority` | Enum: `LOW`, `MEDIUM`, `HIGH` | 400 `"Priority must be one of: ..."` |
| `dueDate` | Today or future (`@FutureOrPresent`) | 400 `"Due date must be today or in the future"` |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+ (for GitHub MCP server — `npx` must be on PATH)

### Run the API

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskflowdb`).

### Run tests

```bash
mvn test
```

### Run the Postman demo

Import `postman/TaskFlow-Demo.postman_collection.json` into Postman, then run the collection with the **Collection Runner**. It covers the full flow in order:

1. Create a task → captures `taskId`
2. CRUD operations on the task
3. Add / update / delete notes → captures `noteId`
4. Ask the AI two questions about the codebase
5. Validation error showcase (400s and 404s)
6. Cleanup (delete the task)

The `baseUrl` collection variable defaults to `http://localhost:8080`. Start the app first with `mvn spring-boot:run`.

---

### Set up the GitHub MCP server

1. Generate a GitHub Personal Access Token with `repo` scope
2. Add it to `.vscode/mcp.json` (VS Code) and/or `.claude/settings.json` (Claude Code CLI)
3. Neither file should ever be committed — both are in `.gitignore`

---

## Development Journey (AI-Native Days)

| Day | Focus | Key AI Tool | Outcome |
|---|---|---|---|
| 1 | Spring Boot scaffold + CRUD | Claude Code | Entity, repository, service, controller, error handler |
| 2 | Pagination, filtering, Swagger | Claude Code | Paginated endpoints, OpenAPI docs |
| 3 | Input validation + unit tests | Claude Code + Copilot | `@Valid` DTOs, `GlobalExceptionHandler`, 26+ tests |
| 4 | GitHub MCP issue-driven feature | Claude Code + **GitHub MCP** | `dueDate` field implemented from issue #3 without re-typing requirements |
| 5 | Notes sub-resource | Claude Code + specialist agents | Full Notes CRUD (`/api/tasks/{id}/notes/**`) with validation and tests |
| 6 | RAG over codebase | Claude Code + **EPAM DIAL AI Proxy** | `POST /api/ai/ask` — answers questions about the codebase using `gpt-5-mini-2025-08-07` |
| 7 | End-to-end management showcase | GitHub Actions + Postman | CI pipeline on every push/PR; Postman collection covering full Task → Notes → AI → Validation flow |

---

## Real Use Case for Management Showcase

### The Story: "From GitHub Issue to Production Code in 5 Minutes"

> *A developer creates a GitHub issue describing a new feature. An AI agent reads the issue, implements every requirement, writes tests, commits with a traceable message, and opens a PR — all without the developer typing a single line of code.*

This is exactly what happened on Day 4.

---

### Why This Matters to the Business

| Traditional workflow | AI-native workflow |
|---|---|
| Developer reads ticket, opens IDE, codes feature | AI agent reads ticket via MCP, codes feature |
| Developer writes tests manually | Tests generated alongside implementation |
| Developer creates commit and PR with ticket reference | Commit + PR created automatically with issue link |
| Review cycle starts hours or days later | PR opens in minutes |

**The velocity gain is not about replacing developers** — it's about eliminating the low-value, high-friction steps so developers spend their time on architecture, review, and judgment.

---

### Showcase Demo Script (10 minutes)

1. **Show the GitHub issue (#3)** — requirements written in plain English
2. **Run Claude Code** — the agent connects to GitHub via MCP and reads the issue as a tool call (not copy-paste)
3. **Watch the implementation** — entity, DTO, mapper, repository, tests — all generated and committed live
4. **Show the PR (#4)** — auto-linked to the issue, ready for review
5. **Highlight traceability** — commit message `closes #3`, PR body has acceptance criteria checkboxes

**Key talking point:** The AI doesn't just generate boilerplate — it reads the project's own conventions (`.github/copilot-instructions.md`) and enforces them in every file it touches.

---

### Proposed Real Use Cases for Production Adoption

| Use Case | AI Tools | Business Value |
|---|---|---|
| **JIRA / Linear ticket → code** | MCP (JIRA/Linear) + Claude Code | Same Day 4 pattern at enterprise scale |
| **API contract validation** | Claude Code + OpenAPI | Catch breaking changes before review |
| **Automated PR description** | Claude Code + GitHub MCP | Consistent, traceable PRs from every commit |
| **Legacy code explanation** | Claude Code RAG (Day 6) | Onboard new developers in hours, not weeks |
| **Test coverage enforcement** | Claude Code | Every PR automatically assessed for missing tests |

The strongest showcase candidate is the **"JIRA ticket → PR in minutes"** story, which maps directly to what Day 4 demonstrated using GitHub issues.
