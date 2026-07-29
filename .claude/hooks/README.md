# TaskFlow — Claude Code Hooks

Hooks are shell commands that fire automatically when Claude Code performs certain actions.
They act as **AI quality gates** — enforcing compile checks and test runs without any manual step.

## Available Hooks

| Script | Fires | Action |
|---|---|---|
| `post-java-edit.sh` | After any `.java` file is edited | `mvn compile -q` — catch compile errors immediately |
| `pre-commit.sh` | Before a `git commit` bash command | `mvn test -q` — block the commit if tests fail |

## Setup

Hooks are configured in `.claude/settings.json` (not committed — this file contains your PAT).
Add the `hooks` section alongside your `mcpServers` configuration.

### Complete settings.json with hooks enabled

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
  },
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit",
        "hooks": [
          {
            "type": "command",
            "command": "bash \".claude/hooks/post-java-edit.sh\"",
            "timeout": 30000
          }
        ]
      },
      {
        "matcher": "Write",
        "hooks": [
          {
            "type": "command",
            "command": "bash \".claude/hooks/post-java-edit.sh\"",
            "timeout": 30000
          }
        ]
      }
    ],
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "bash \".claude/hooks/pre-commit.sh\"",
            "timeout": 60000
          }
        ]
      }
    ]
  }
}
```

## How Hooks Work

```
Claude Code calls Edit tool on Task.java
         │
         ▼
PostToolUse fires → post-java-edit.sh runs
         │
         ├── mvn compile succeeds → exit 0 → Claude continues
         │
         └── mvn compile fails   → exit 1 → Claude sees error output
                                             and fixes the problem
                                             before moving on
```

```
Claude Code calls Bash("git commit -m '...'")
         │
         ▼
PreToolUse fires → pre-commit.sh runs
         │
         ├── mvn test passes → exit 0 → git commit executes
         │
         └── mvn test fails  → exit 1 → commit is blocked
                                         Claude sees which tests failed
                                         and fixes them first
```

## Why This Matters for the Demo

Hooks turn Claude Code from a "write code and hope" tool into a
**self-correcting agent**. When shown live:

1. Claude edits a Java file
2. The hook auto-fires and prints `✓ Compile succeeded`
3. If Claude makes a mistake, the hook prints `✗ Compile FAILED`
4. Claude reads the error, fixes the file, and the hook passes on the next edit

No developer intervention. No separate terminal. The AI validates its own work.

## Environment Variables Available in Hook Scripts

| Variable | Value |
|---|---|
| `CLAUDE_FILE_PATHS` | Space-separated list of files affected by the tool call |
| `CLAUDE_TOOL_NAME` | Name of the tool that fired the hook |
| `CLAUDE_TOOL_INPUT` | JSON-encoded input to the tool |
