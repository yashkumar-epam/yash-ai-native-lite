---
name: validate-conventions
description: Use this skill when the user says "check conventions", "validate my code", "are these files correct", "run a convention audit", or before opening a PR. Reads all Java source files and reports every violation of the rules in CLAUDE.md and .github/copilot-instructions.md.
allowed-tools: [Read, Glob, Grep, Bash]
version: 1.0.0
---

# Validate Conventions

Audits every Java source file in the project against the conventions defined in
`CLAUDE.md` and `.github/copilot-instructions.md`. Reports violations with file,
line reference, and fix instruction.

## When This Skill Activates

- `/validate-conventions`
- User says "check conventions", "audit my code", "is everything correct?"
- Before creating a PR
- After a large refactor

## Workflow

### Step 1 — Discover all Java source files

```
Glob: src/main/java/**/*.java
Glob: src/test/java/**/*.java
```

### Step 2 — Run all convention checks

For each file, check the following rules. Report every violation found.

---

#### Rule 1: No Field Injection

```
Grep: @Autowired
      (in src/main/**/*.java)
```

Any `@Autowired` field is a violation.
**Fix:** replace with constructor injection via `@RequiredArgsConstructor` or explicit constructor.

---

#### Rule 2: ResponseEntity on All Endpoints

```
Grep: public .* @(Get|Post|Put|Delete|Patch)Mapping
```

Every controller method must return `ResponseEntity<T>`.
**Fix:** wrap return type: `ResponseEntity<TaskResponseDTO>`.

---

#### Rule 3: @Slf4j on Every Class

```
Grep: ^public class
      (check each matched file for @Slf4j in its annotations)
```

Every `@Service`, `@RestController`, `@Component`, `@Repository` class must have `@Slf4j`.

---

#### Rule 4: @Transactional on Write Methods

```
Grep: public TaskResponseDTO createTask|updateTask|deleteTask
      (in service classes)
```

Methods that create, update, or delete data must have `@Transactional`.

---

#### Rule 5: No Entity in Controller Return Type

```
Grep: ResponseEntity<Task>
      (in controller classes)
```

Any `ResponseEntity<Task>` (raw entity) is a violation. Must be `ResponseEntity<TaskResponseDTO>`.

---

#### Rule 6: @Valid on Controller Parameters

```
Grep: @RequestBody TaskRequestDTO
```

Every `@RequestBody TaskRequestDTO` must be preceded by `@Valid`.
**Fix:** change to `@Valid @RequestBody TaskRequestDTO`.

---

#### Rule 7: GlobalExceptionHandler — No Try/Catch in Service

```
Grep: catch (
      (in src/main/**/service/**/*.java)
```

Service methods must not catch exceptions themselves — all handling goes through
`GlobalExceptionHandler`. Exception: catch-and-rethrow is allowed only if enriching the message.

---

#### Rule 8: @MockBean in @WebMvcTest Tests

```
Grep: @Mock
      (in src/test/**/*ValidationTest.java or *ControllerTest.java)
```

`@Mock` in a `@WebMvcTest` class is a violation — the mock won't be wired.
**Fix:** replace with `@MockBean` and import `org.springframework.boot.test.mock.mockito.MockBean`.

---

#### Rule 9: Validation Messages Match Standards

```
Grep: message = "
      (in src/main/**/dto/**/*.java)
```

Every `message = "..."` string must exactly match one of the values in the
standards table in `CLAUDE.md`. Flag any message that doesn't match.

---

#### Rule 10: @WebMvcTest Correct Import

```
Grep: import org.springframework.boot.webmvc
      (in src/test/**/*.java)
```

`org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` is the **wrong** package.
**Fix:** use `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`.

---

### Step 3 — Report

Format output as:

```
VIOLATIONS FOUND: N

[RULE]  <Rule name>
  File: <relative path>
  Line: <approximate line or context>
  Issue: <description>
  Fix: <how to fix>

---
SUMMARY
  Total violations: N
  Files with violations: N / total
  Convention compliance: X%
```

If zero violations: print `ALL CONVENTIONS SATISFIED ✓`.
