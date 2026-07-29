#!/usr/bin/env bash
# post-java-edit.sh
# Runs after Claude Code edits any .java file.
# Immediately compiles the project to surface errors before the next step.
#
# Configured in .claude/settings.json under hooks.PostToolUse.
# See .claude/hooks/README.md for setup instructions.

set -euo pipefail

PROJECT_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"

# Only run if the edited file is a Java source file
EDITED_FILES="${CLAUDE_FILE_PATHS:-}"
if [[ -z "$EDITED_FILES" ]]; then
  exit 0
fi

# Check if any edited file is a .java file
JAVA_EDITED=false
for f in $EDITED_FILES; do
  if [[ "$f" == *.java ]]; then
    JAVA_EDITED=true
    break
  fi
done

if [[ "$JAVA_EDITED" == false ]]; then
  exit 0
fi

echo "[hook:post-java-edit] Java file changed — running mvn compile..."

cd "$PROJECT_ROOT"
if mvn compile -q 2>&1; then
  echo "[hook:post-java-edit] ✓ Compile succeeded"
  exit 0
else
  echo "[hook:post-java-edit] ✗ Compile FAILED — fix errors before continuing"
  exit 1
fi
