---
name: api-code-review
description: Use this skill when the user says "review my changes", "check this PR", "is this code correct", "review the API", or when about to commit or push. Runs a multi-dimension review of the current git diff covering REST conventions, validation, test coverage, and Spring Boot best practices.
argument-hint: [branch-or-file]
allowed-tools: [Read, Glob, Grep, Bash, PowerShell]
version: 1.0.0
---

# API Code Review

Multi-dimension code review for the TaskFlow Spring Boot API.
Checks conventions, validation, test coverage, and API design in one pass.

## When This Skill Activates

- `/api-code-review`
- User says "review my changes", "check my PR", "is this code good?"
- Before a `git push` or PR creation

## Workflow

### Step 1 — Gather changed files

```bash
git diff HEAD --name-only --diff-filter=AM
git diff HEAD -- '*.java'
```

If a specific file or branch is given in `$ARGUMENTS`, scope the review to that.

### Step 2 — Run all four review dimensions

Review each dimension independently and report findings:

---

#### Dimension 1: Spring Boot Conventions

Check every changed `.java` file for:

- [ ] Constructor injection — no `@Autowired` anywhere
- [ ] All endpoints return `ResponseEntity<T>`
- [ ] `@Slf4j` present on every class
- [ ] Write methods annotated with `@Transactional`
- [ ] Package matches feature (not layer) structure
- [ ] No JPA entity returned from controller (only DTOs)
- [ ] All errors go through `GlobalExceptionHandler`

---

#### Dimension 2: Input Validation

Check every changed DTO and controller for:

- [ ] Controller params annotated with `@Valid`
- [ ] All DTO fields have appropriate constraints
- [ ] Validation messages match the standards table in `CLAUDE.md`
- [ ] `@FutureOrPresent` on any date field
- [ ] No custom validation bypassing the global handler

---

#### Dimension 3: Test Coverage

For every changed source class, check:

- [ ] Corresponding test class exists and was updated
- [ ] Happy path tested
- [ ] Error path tested (`ResourceNotFoundException`, HTTP 400, HTTP 404)
- [ ] New field / method has at least one test
- [ ] `@MockBean` used (not `@Mock`) in `@WebMvcTest` classes

---

#### Dimension 4: API Design Quality

Check REST endpoints for:

- [ ] Correct HTTP status codes (201 POST, 204 DELETE, 200 GET/PUT)
- [ ] Plural noun paths (`/api/tasks`, not `/api/task`)
- [ ] `@Operation` and `@ApiResponses` present on public endpoints
- [ ] No breaking change to existing endpoint paths or response shapes
- [ ] Pagination returns `PagedResponseDTO`, not raw `Page<T>`

---

### Step 3 — Report findings

Format each finding as:

```
[SEVERITY] <Dimension> — <File>:<line>
  Issue: <what's wrong>
  Fix:   <how to fix it>
```

Severity levels:
- **HIGH** — will cause runtime error, test failure, or violates a hard convention rule
- **MEDIUM** — violates a convention but won't break at runtime
- **LOW** — style / completeness suggestion

### Step 4 — Summary

End with a table:

| Dimension | Status | HIGH | MEDIUM | LOW |
|---|---|---|---|---|
| Conventions | PASS / FAIL | N | N | N |
| Validation | PASS / FAIL | N | N | N |
| Tests | PASS / FAIL | N | N | N |
| API Design | PASS / FAIL | N | N | N |

**READY TO MERGE** if all dimensions PASS with zero HIGH findings.
