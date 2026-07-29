---
name: github-mcp-issue-dev
description: Use this skill when the user says "implement issue #N", "read the GitHub issue and build it", "use MCP to read issue", or asks to drive development from a GitHub issue. Connects to the GitHub MCP server, reads the issue, implements every requirement, and creates a commit + PR that closes the issue.
argument-hint: <issue-number>
allowed-tools: [Read, Glob, Grep, Edit, Write, Bash, PowerShell]
version: 1.0.0
---

# GitHub MCP Issue-Driven Development

This skill implements the **Day 4 AI-native workflow**: read a GitHub issue via the MCP server and implement it end-to-end — without the developer re-typing a single requirement.

## When This Skill Activates

- User says `/github-mcp-issue-dev <issue-number>`
- User says "implement issue #N", "build what issue #N describes"
- User says "read the GitHub issue via MCP and implement it"

## Prerequisite

The GitHub MCP server must be configured in `.claude/settings.json` (local, never committed):

```json
{
  "mcpServers": {
    "github": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": "<your-PAT>"
      }
    }
  }
}
```

## Step-by-Step Workflow

### Step 1 — Read the issue via MCP

Use the GitHub MCP server tool to fetch the issue. Do **not** ask the user to paste the issue text — call the MCP tool directly:

```
repo: yashkumar-epam/yash-ai-native-lite
issue_number: <argument>
```

Extract from the issue:
- **Title** — the feature name
- **Requirements** — each bullet point is a file/class to modify
- **Acceptance criteria** — these become the test assertions

### Step 2 — Identify all files to change

Read the following files before touching anything:
- `src/main/java/**/model/*.java`
- `src/main/java/**/dto/*.java`
- `src/main/java/**/mapper/*.java`
- `src/main/java/**/repository/*.java`
- `src/main/java/**/service/*.java`
- `src/main/java/**/controller/*.java`
- `src/test/**/*Test.java`

### Step 3 — Implement every requirement

Apply changes in this order (dependencies flow downward):

1. **Entity** (`model/Task.java`) — add the field, import, `@Column`
2. **Request DTO** (`dto/TaskRequestDTO.java`) — add validation annotation, import
3. **Response DTO** (`dto/TaskResponseDTO.java`) — add the field
4. **Mapper** (`mapper/TaskMapper.java`) — map the field in `toResponseDTO`, `toEntity`, and `updateEntityFromDTO`
5. **Repository** (`repository/TaskRepository.java`) — add any query methods mentioned in the issue
6. **Tests** — update builders to include the new field; add tests for each acceptance criterion

Validation messages must match the standards table in `.github/copilot-instructions.md`.

### Step 4 — Commit with issue reference

```
feat: <title from issue> - closes #<issue-number>

<2-3 line summary of what changed>

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
```

The `closes #N` keyword causes GitHub to auto-close the issue when the PR is merged.

### Step 5 — Push and create a PR

```bash
git push origin <current-branch>
```

Then create a PR via GitHub API (or `gh pr create`) with:
- Title: `feat: <issue title> (<day> tag)`
- Body: link to the issue, acceptance criteria as checkboxes, test plan

## Example Invocation

```
/github-mcp-issue-dev 3
```

This reads issue #3 ("Add due-date field to Task"), implements `dueDate` across entity/DTOs/mapper/repository/tests, commits `closes #3`, and opens a PR.

## What Was Demonstrated in Day 4

| Step | Tool Used | Output |
|---|---|---|
| Create issue | GitHub REST API | Issue #3 opened |
| Read issue | **GitHub MCP server** (`get_issue` tool) | Requirements extracted without copy-paste |
| Implement | Claude Code (file edits) | 7 files changed |
| Commit | `git commit` | `closes #3` in message |
| PR | GitHub REST API | PR #4 auto-linked to issue |
