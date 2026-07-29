#!/usr/bin/env bash
# pre-commit.sh
# Runs before Claude Code executes a `git commit` command.
# Ensures all tests pass before any commit is created.
#
# Configured in .claude/settings.json under hooks.PreToolUse.
# See .claude/hooks/README.md for setup instructions.

set -euo pipefail

PROJECT_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"

# Only intercept git commit commands
TOOL_INPUT="${CLAUDE_TOOL_INPUT:-}"
if [[ "$TOOL_INPUT" != *"git commit"* ]]; then
  exit 0
fi

echo "[hook:pre-commit] Running tests before commit..."

cd "$PROJECT_ROOT"

if mvn test -q 2>&1; then
  echo "[hook:pre-commit] ✓ All tests passed — commit allowed"
  exit 0
else
  echo "[hook:pre-commit] ✗ Tests FAILED — commit blocked"
  echo "[hook:pre-commit] Fix failing tests before committing."
  exit 1
fi
