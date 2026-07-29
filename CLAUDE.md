# TaskFlow API — Claude Code Instructions

This file is auto-loaded by Claude Code on every conversation in this project.
It defines conventions, available AI capabilities, and the development workflow.

---

## Project Context

Spring Boot 3.x REST API for task management, built as an AI-native showcase.
**Repo:** `yashkumar-epam/yash-ai-native-lite`

The project demonstrates: Claude Code + GitHub Copilot + GitHub MCP server +
multi-agent workflows + automated hooks — a full AI-native SDLC.

---

## Quick Reference — All AI Capabilities

### Slash Commands (User-Invoked Skills)

| Command | What it does |
|---|---|
| `/github-mcp-issue-dev <N>` | Read GitHub issue #N via MCP and implement it end-to-end |
| `/generate-tests <ClassName>` | Generate comprehensive tests for a service or controller |
| `/api-code-review` | Multi-dimension review of current staged changes |
| `/add-endpoint <method> <path>` | Scaffold a new REST endpoint following all conventions |
| `/validate-conventions` | Audit all Java files against project rules; report violations |

### Multi-Agent Workflows

| Name | Args | What it orchestrates |
|---|---|---|
| `feature-implementation` | `{issueNumber: N}` | Research → Plan → Parallel implementation per layer → Tests → Review |
| `code-review` | _(none)_ | 4 parallel agents: conventions, validation, tests, API design |
| `test-generation` | `{className?}` | Gap analysis → parallel test writers → quality validation |

Run a workflow: ask Claude Code `"run the feature-implementation workflow for issue #5"`.

### Specialist Agents

| Agent type | Use when |
|---|---|
| `taskflow-architect` | Architecture decisions, package design, dependency trade-offs |
| `taskflow-reviewer` | Code-quality review before merging |
| `taskflow-tester` | Writing or improving JUnit 5 / Mockito / MockMvc tests |

---

## Coding Conventions (apply to every generated file)

1. Constructor injection only — never `@Autowired`, never field injection
2. All endpoints return `ResponseEntity<T>`
3. Package by feature, not by layer
4. DTOs for all request/response — entities never leave the service layer
5. `@Slf4j` on every class
6. `@Transactional` on all create / update / delete service methods
7. Validation errors → HTTP 400 with a clear, user-friendly message
8. Every new feature includes unit tests
9. REST naming: plural nouns, lowercase, hyphen-separated paths
10. All exception handling through `GlobalExceptionHandler` only
11. `@MockBean` (never `@Mock`) in every `@WebMvcTest` test class
12. Validation messages must match the standards table exactly

### Validation Message Standards Table

| Field | Constraint | Exact message |
|---|---|---|
| `title` | blank / null / empty | `"Title is required"` |
| `title` | > 255 chars | `"Title cannot exceed 255 characters"` |
| `description` | > 500 chars | `"Description cannot exceed 500 characters"` |
| `status` | not in enum | `"Status must be one of: TODO, IN_PROGRESS, DONE"` |
| `priority` | not in enum | `"Priority must be one of: LOW, MEDIUM, HIGH"` |
| `dueDate` | past date | `"Due date must be today or in the future"` |

---

## Package Structure

```
com.epam.taskflow.taskflow_api
├── config/       OpenApiConfig
├── controller/   TaskController  (@RestController, /api/tasks/**)
├── dto/          TaskRequestDTO, TaskResponseDTO, PagedResponseDTO
├── exception/    GlobalExceptionHandler, ResourceNotFoundException
├── mapper/       TaskMapper  (entity ↔ DTO, three methods)
├── model/        Task  (JPA entity, never exposed via API)
├── repository/   TaskRepository  (JPA + custom queries)
└── service/      TaskService  (@Transactional writes)
```

---

## GitHub MCP Server

Configured in `.claude/settings.json` (not committed — contains PAT).
Provides live tool access to GitHub without copy-pasting text.

Available MCP tools: `get_issue`, `create_issue`, `create_pull_request`,
`list_issues`, `list_pull_requests`, `get_pull_request`, `list_commits`.

**Issue-driven development pattern (repeat for every feature):**
1. Create a GitHub issue with title + requirements + acceptance criteria
2. `/github-mcp-issue-dev <issue-number>`
3. Claude reads the issue via MCP, implements, commits `closes #N`, opens PR

---

## Hooks (Auto Quality Gates)

Hook scripts live in `.claude/hooks/`. Configure them in `.claude/settings.json`
using the template at `.claude/hooks/README.md`.

| Hook | Fires | Action |
|---|---|---|
| `post-java-edit` | After any `.java` file is edited | `mvn compile -q` — catch errors immediately |
| `pre-commit` | Before `git commit` | `mvn test -q` — block commit if tests fail |

---

## AI-Native Day Tracker

| Day | Status | Feature | Primary AI Tool |
|---|---|---|---|
| 1 | ✅ Done | Spring Boot scaffold + CRUD | Claude Code |
| 2 | ✅ Done | Pagination, filtering, OpenAPI | Claude Code |
| 3 | ✅ Done | Input validation + 26+ unit tests | Claude Code + GitHub Copilot |
| 4 | ✅ Done | `dueDate` field via GitHub MCP issue #3 | Claude Code + GitHub MCP |
| 5 | ⏳ | Upcoming | |
| 6 | ⏳ | RAG over codebase (Day 6 needs this README) | |
| 7 | ⏳ | End-to-end management showcase | |
