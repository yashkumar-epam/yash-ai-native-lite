# TaskFlow API — Copilot Instructions

These instructions are loaded automatically by **GitHub Copilot** and **Claude Code** for every change in this repository. All AI tools must respect these conventions without being reminded.

---

## Coding Conventions

1. Always use constructor injection. Never use field injection or `@Autowired`.
2. All endpoints must return `ResponseEntity<T>`.
3. Organize code by feature, not by layer.
4. Always use DTOs for request and response models. Never expose JPA entities directly through the API.
5. Use `@Slf4j` for logging in every class.
6. All service methods that create, update, or delete data must use `@Transactional`.
7. Validation errors must return HTTP 400 with a clear, user-friendly message.
8. Every new feature must include unit tests.
9. Follow REST naming conventions: plural nouns, lowercase paths, and hyphen-separated words where needed.
10. All exception handling must go through `GlobalExceptionHandler` only.
11. Never add `@NotNull` alongside `@NotBlank` — `@NotBlank` already handles null. Use consistent messages across DTO and tests.
12. In `@WebMvcTest` classes, always use `@MockBean` (not `@Mock`) for service dependencies so they are wired into the Spring context.

---

## AI Tools & Skills Active in This Project

### Claude Code CLI

The primary AI assistant. Invoked via the Claude Code CLI or VS Code extension.

- **Skill: `claude-api`** — Loaded when building LLM-powered features. Provides Anthropic SDK patterns, model defaults (`claude-opus-4-8`), streaming, and tool-use examples for Java/Spring Boot.
- **MCP: GitHub server** — Configured in `.claude/settings.json`. Gives Claude Code authenticated access to GitHub issues, PRs, and repositories as native tools. Used for issue-driven development (see Day 4).

Configuration file: `.claude/settings.json` (**never commit — contains PAT token**)

### GitHub Copilot

In-IDE AI autocomplete for VS Code and JetBrains. Reads this file on every suggestion to enforce the conventions above.

### GitHub MCP Server (`@modelcontextprotocol/server-github`)

Connects Claude Code to the GitHub API over the Model Context Protocol (MCP).

**Setup (VS Code):** `.vscode/mcp.json` (**never commit — contains PAT token**)

```json
{
  "servers": {
    "github": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": "<your-PAT-here>"
      }
    }
  }
}
```

**What MCP enables:** An AI agent can call `get_issue`, `create_pull_request`, `list_commits`, etc. directly — the same way it calls any other tool. No copy-pasting issue text; the agent reads it from GitHub in real time.

**Day 4 pattern (use this for every feature going forward):**
1. Create a GitHub issue with title, requirements, and acceptance criteria
2. Ask Claude Code: *"Read issue #N via MCP and implement it"*
3. The agent reads the issue, implements the feature, commits `closes #N`, and opens a PR

---

## Package Structure

```
com.epam.taskflow.taskflow_api
├── config/          # OpenApiConfig
├── controller/      # REST endpoints
├── dto/             # Request + Response DTOs only
├── exception/       # GlobalExceptionHandler, ResourceNotFoundException
├── mapper/          # Entity ↔ DTO
├── model/           # JPA entities
├── repository/      # Spring Data JPA interfaces
└── service/         # Business logic
```

---

## Validation Message Standards

These exact messages are tested — do not change them without updating tests:

| Field | Constraint | Message |
|---|---|---|
| `title` | blank / null / empty | `"Title is required"` |
| `title` | > 255 chars | `"Title cannot exceed 255 characters"` |
| `description` | > 500 chars | `"Description cannot exceed 500 characters"` |
| `status` | invalid enum | `"Status must be one of: TODO, IN_PROGRESS, DONE"` |
| `priority` | invalid enum | `"Priority must be one of: LOW, MEDIUM, HIGH"` |
| `dueDate` | past date | `"Due date must be today or in the future"` |

