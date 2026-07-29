---
name: generate-tests
description: Use this skill when the user says "generate tests", "write tests for", "add test coverage", "create unit tests for <ClassName>", or when test coverage gaps are identified. Produces JUnit 5 + Mockito tests following the project's existing patterns.
argument-hint: <ClassName>
allowed-tools: [Read, Glob, Grep, Edit, Write, Bash]
version: 1.0.0
---

# Generate Tests

Generates comprehensive JUnit 5 unit and validation tests for TaskFlow classes.

## When This Skill Activates

- `/generate-tests TaskService`
- `/generate-tests TaskController`
- User says "write tests for X", "I need test coverage for Y"
- After implementing a new method or feature with no corresponding tests

## Arguments

`$ARGUMENTS` — the class name to test (e.g. `TaskService`, `TaskController`, `TaskMapper`).
If no argument is provided, analyze both `TaskServiceTest` and `TaskControllerValidationTest` for gaps.

## Workflow

### 1 — Read the source and existing test

```
Read: src/main/java/com/epam/taskflow/taskflow_api/<layer>/<ClassName>.java
Read: src/test/java/com/epam/taskflow/taskflow_api/<layer>/<ClassName>Test.java
```

Identify:
- Every public method in the source class
- Which methods already have tests
- Which edge cases are missing (ResourceNotFoundException, null inputs, boundary values)

### 2 — Determine test pattern

**For service classes** (`@Service`):
- Use `@ExtendWith(MockitoExtension.class)`
- `@Mock TaskRepository`, `@Mock TaskMapper`
- `@InjectMocks TaskService`
- Test happy path + `ResourceNotFoundException` for every `findById` call
- Verify mock interactions with `verify(mock, times(1)).method(...)`

**For controller classes** (`@RestController`):
- Use `@WebMvcTest(TaskController.class)`
- `@MockBean TaskService` ← **always `@MockBean`, never `@Mock`**
- `@Autowired MockMvc`, `@Autowired ObjectMapper`
- Test: blank/null/empty fields, boundary sizes, invalid enums, past `dueDate`
- Use `jsonPath("$.status").value(400)` and `jsonPath("$.message", containsString(...))`

**For mapper classes** (`@Component`):
- Plain JUnit, no Spring context
- Test `toResponseDTO`, `toEntity`, `updateEntityFromDTO` with null inputs and valid inputs
- Assert every field is correctly mapped

### 3 — Generate test methods

For each gap, write a method following this template:

```java
@Test
@DisplayName("Should <expected behavior> when <condition>")
void test<MethodName>_<scenario>() {
    // Arrange
    // ... builders with all fields including dueDate

    // Act
    // ... call method under test

    // Assert
    // ... assertNotNull / assertEquals / assertThrows
    // ... verify(mock, times(1)).method(...)
}
```

### 4 — LocalDate handling

Always use dynamic dates to avoid test staleness:
- **Valid future date:** `LocalDate.now().plusDays(7)`
- **Today (valid):** `LocalDate.now()`
- **Past (invalid for @FutureOrPresent):** `LocalDate.of(2020, 1, 1)`

In controller tests, serialize as ISO string: `"dueDate": "2026-08-15"`.

### 5 — Insert tests into the existing file

Use `Edit` to add new test methods before the closing `}` of the test class.
Do not create a new file — append to the existing test class.

## Coverage Targets

| Class | Target scenarios |
|---|---|
| `TaskService` | getAllTasks, getAllTasksPaged, getById (found/not found), create, update (found/not found), delete (found/not found), getByStatus, getByPriority, search |
| `TaskController` | POST valid, POST invalid each field, PUT valid, PUT invalid, DELETE 204, GET 200, GET 404 |
| `TaskMapper` | toResponseDTO (null/valid), toEntity (null/valid), updateEntityFromDTO (null DTO, null task, valid) |
