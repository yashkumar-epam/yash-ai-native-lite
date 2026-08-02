# How the RAG API Works — `POST /api/ai/ask`

This document explains the **Retrieval-Augmented Generation (RAG)** endpoint
built into the TaskFlow Spring Boot API. It is written to be read by anyone —
no prior AI experience required.

---

## Table of Contents

1. [What is RAG?](#1-what-is-rag)
2. [Architecture Overview](#2-architecture-overview)
3. [End-to-End Request Flow](#3-end-to-end-request-flow)
4. [Code Walkthrough](#4-code-walkthrough)
5. [Request & Response Examples](#5-request--response-examples)
6. [Why EPAM DIAL Proxy?](#6-why-epam-dial-proxy)
7. [Design Decisions & Trade-offs](#7-design-decisions--trade-offs)
8. [Limitations](#8-limitations)
9. [One-Line Pitch](#9-one-line-pitch)

---

## 1. What is RAG?

**RAG = Retrieval-Augmented Generation**

A standard Large Language Model (LLM) answers from its training data only.
It does not know your codebase, your business rules, or anything that happened
after its training cutoff.

RAG solves this by doing two things before calling the model:

| Step | Name | What it does |
|---|---|---|
| 1 | **Retrieval** | Fetch relevant documents from a known source |
| 2 | **Augmented Generation** | Inject those documents into the prompt, then ask the model |

The model now answers **based on the documents you gave it**, not just its
training data. The answer is grounded in real, up-to-date information.

In this project, the "documents" are the **Java source files** of the TaskFlow
API itself. You ask a question about the codebase and the model reads the
actual code before answering.

---

## 2. Architecture Overview

```
  Client (Postman / curl / frontend)
          |
          | POST /api/ai/ask
          | { "question": "What does TaskController do?" }
          |
          v
  +-------------------------+
  |    AiController.java    |   Validates the request (@NotBlank, max 1000 chars)
  |  /api/ai/ask            |   Returns 400 if invalid
  +-------------------------+
          |
          v
  +-------------------------+
  |     AiService.java      |
  |                         |
  |  1. loadSourceFiles()   |---> Scans src/main/java/**/*.java
  |                         |     (up to 20 files, sorted A-Z)
  |  2. buildContext()      |---> Reads & concatenates file contents
  |                         |     (hard cap: 50,000 characters)
  |  3. queryModel()        |---> Builds chat messages + calls DIAL
  +-------------------------+
          |
          | HTTPS POST
          | Api-Key header
          v
  +------------------------------------------+
  |   EPAM DIAL AI Proxy                     |
  |   https://ai-proxy.lab.epam.com          |
  |                                          |
  |   /openai/deployments/                   |
  |     gpt-5-mini-2025-08-07/               |
  |     chat/completions                     |
  |                                          |
  |   OpenAI-compatible API                  |
  +------------------------------------------+
          |
          | JSON response { choices[0].message.content }
          v
  +-------------------------+
  |     AiService.java      |   Extracts the answer string
  +-------------------------+
          |
          v
  +-------------------------+
  |    AiController.java    |   Wraps in AiQueryResponseDTO
  +-------------------------+
          |
          v
  Client receives:
  {
    "answer": "TaskController is a REST controller...",
    "model": "gpt-5-mini-2025-08-07",
    "contextFilesUsed": 20
  }
```

---

## 3. End-to-End Request Flow

### Step 1 — Request arrives & is validated

The client sends:

```http
POST /api/ai/ask
Content-Type: application/json

{ "question": "How is input validation handled in this project?" }
```

`AiController` receives it and runs `@Valid` on `AiQueryRequestDTO`:
- `question` must not be blank → 400 `"Question is required"`
- `question` must be ≤ 1000 characters → 400 `"Question cannot exceed 1000 characters"`

---

### Step 2 — Source files are loaded (the Retrieval step)

`AiService.loadSourceFilePaths()` walks:

```
<project_root>/src/main/java/**/*.java
```

It collects up to **20 files**, sorted alphabetically. On a typical run these
are all the Java files in the project (controllers, services, DTOs, mappers,
models, etc.).

```
AiConfig.java
AiController.java
AiQueryRequestDTO.java
AiQueryResponseDTO.java
AiService.java
GlobalExceptionHandler.java
Note.java
NoteController.java
NoteMapper.java
NoteRepository.java
NoteRequestDTO.java
NoteResponseDTO.java
NoteService.java
OpenApiConfig.java
ResourceNotFoundException.java
Task.java
TaskController.java
TaskMapper.java
TaskRepository.java
TaskService.java
```

---

### Step 3 — Context is built (still Retrieval)

`AiService.buildContext()` reads each file and concatenates them into one
string with a separator header per file:

```
=== AiConfig.java ===
package com.epam.taskflow...
...full file content...

=== AiController.java ===
package com.epam.taskflow...
...full file content...
```

**Hard cap:** 50,000 characters total. If a file would push the total over the
limit its content is truncated and tagged `// [truncated]`. This prevents the
prompt from exceeding the model's context window.

---

### Step 4 — Prompt is assembled

`AiService.queryModel()` builds two messages for the chat model:

**System message** (sets the model's role):
```
You are an expert on the TaskFlow Spring Boot REST API codebase.
Answer questions about the code accurately and concisely.
Base your answers on the provided Java source files.
```

**User message** (the actual prompt):
```
Context from the codebase:

=== AiConfig.java ===
...50k of Java source code...

Question: How is input validation handled in this project?
```

---

### Step 5 — The model is called (the Generation step)

`AiService` sends an HTTP POST to the EPAM DIAL AI Proxy using Spring's
`RestClient`:

```
POST https://ai-proxy.lab.epam.com
     /openai/deployments/gpt-5-mini-2025-08-07
     /chat/completions?api-version=2025-04-01-preview

Headers:
  Content-Type: application/json
  Api-Key: <dial-api-key>

Body:
{
  "messages": [
    { "role": "system", "content": "<system prompt>" },
    { "role": "user",   "content": "<context + question>" }
  ],
  "max_completion_tokens": 2048
}
```

---

### Step 6 — Answer is extracted & returned

The DIAL proxy returns an OpenAI-format response:

```json
{
  "choices": [
    {
      "message": {
        "role": "assistant",
        "content": "Input validation is handled via Jakarta Validation..."
      }
    }
  ]
}
```

`AiService` pulls out `choices[0].message.content` and wraps it:

```json
{
  "answer": "Input validation is handled via Jakarta Validation...",
  "model": "gpt-5-mini-2025-08-07",
  "contextFilesUsed": 20
}
```

---

## 4. Code Walkthrough

### AiController.java — Entry point

```java
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @PostMapping("/ask")
    public ResponseEntity<AiQueryResponseDTO> ask(
            @RequestBody @Valid AiQueryRequestDTO request) {
        AiQueryResponseDTO response = aiService.askQuestion(request);
        return ResponseEntity.ok(response);
    }
}
```

Thin controller — validates input, delegates everything to `AiService`.

---

### AiService.java — The brain

```java
public AiQueryResponseDTO askQuestion(AiQueryRequestDTO request) {
    // 1. Retrieval — find source files
    List<Path> sourceFiles = loadSourceFilePaths();

    // 2. Retrieval — read and concatenate file contents
    String context = buildContext(sourceFiles);

    // 3. Generation — call the model
    String answer = queryModel(request.getQuestion(), context);

    return AiQueryResponseDTO.builder()
            .answer(answer)
            .model(dialModel)
            .contextFilesUsed(sourceFiles.size())
            .build();
}
```

**`loadSourceFilePaths()`** — scans `src/main/java` for `.java` files,
limits to 20:

```java
Files.walk(sourceRoot)
     .filter(p -> p.toString().endsWith(".java"))
     .sorted()
     .limit(MAX_CONTEXT_FILES)   // 20
     .collect(Collectors.toList());
```

**`buildContext()`** — reads each file, enforces 50k char limit:

```java
for (Path file : files) {
    if (context.length() >= MAX_CONTEXT_CHARS) break;  // 50,000
    String content = Files.readString(file);
    context.append("=== ").append(file.getFileName()).append(" ===\n")
           .append(content).append("\n\n");
}
```

**`queryModel()`** — calls DIAL with system + user messages:

```java
Map<String, Object> body = Map.of(
    "messages", List.of(
        Map.of("role", "system", "content", SYSTEM_PROMPT),
        Map.of("role", "user",   "content", contextualQuestion)
    ),
    "max_completion_tokens", 2048
);

Map<String, Object> response = dialRestClient.post()
    .uri("/openai/deployments/{model}/chat/completions?api-version={v}",
          dialModel, dialApiVersion)
    .body(body)
    .retrieve()
    .body(...);
```

---

### AiConfig.java — Infrastructure

```java
@Bean
public RestClient dialRestClient(
        @Value("${dial.endpoint}") String dialEndpoint,
        @Value("${dial.api-key}") String dialApiKey) {
    return RestClient.builder()
            .baseUrl(dialEndpoint)
            .defaultHeader("Api-Key", dialApiKey)
            .build();
}
```

Creates a reusable `RestClient` bean with the DIAL base URL and API key baked
in as a default header. Every call made through this bean automatically
includes the auth header.

---

### application.properties — Configuration

```properties
dial.endpoint=https://ai-proxy.lab.epam.com
dial.api-key=<your-dial-key>
dial.model=gpt-5-mini-2025-08-07
dial.api-version=2025-04-01-preview
```

Changing the model or endpoint requires only a config change — no code change.

---

## 5. Request & Response Examples

### Happy path

```http
POST /api/ai/ask
Content-Type: application/json

{
  "question": "What does the TaskController do? List all its endpoints."
}
```

```json
HTTP 200 OK

{
  "answer": "TaskController is the REST controller that exposes all Task-related HTTP endpoints under /api/tasks. Supported operations:\n\n- GET /api/tasks — return all tasks\n- POST /api/tasks — create a task (validates TaskRequestDTO, returns 201)\n...",
  "model": "gpt-5-mini-2025-08-07",
  "contextFilesUsed": 20
}
```

---

### Validation error — blank question

```http
POST /api/ai/ask
Content-Type: application/json

{ "question": "" }
```

```json
HTTP 400 Bad Request

{
  "status": 400,
  "error": "Bad Request",
  "message": "Question is required"
}
```

---

### Validation error — question too long

```http
POST /api/ai/ask
Content-Type: application/json

{ "question": "a...a" }   // 1001 characters
```

```json
HTTP 400 Bad Request

{
  "status": 400,
  "message": "Question cannot exceed 1000 characters"
}
```

---

## 6. Why EPAM DIAL Proxy?

The EPAM DIAL AI Proxy (`https://ai-proxy.lab.epam.com`) is an
**OpenAI-compatible API gateway** provided by EPAM for internal use. It:

| Feature | Benefit |
|---|---|
| OpenAI-compatible API | No vendor-specific SDK needed — standard `RestClient` works |
| Centrally managed keys | No individual developer needs an OpenAI/Anthropic account |
| Model routing | Switch models by changing one config property |
| Audit & cost control | EPAM can track usage and costs centrally |
| Enterprise compliance | Traffic stays within EPAM's controlled environment |

The request format is identical to OpenAI's Chat Completions API. The only
difference is the base URL and the `Api-Key` header (instead of
`Authorization: Bearer`).

---

## 7. Design Decisions & Trade-offs

### Why read source files at runtime instead of pre-indexing?

| Approach | Used here? | Trade-off |
|---|---|---|
| Read files at request time | **Yes** | Simple, always up-to-date, slower per request |
| Pre-index into a vector DB | No | Faster at query time, requires extra infrastructure |

For a demo and developer tool, runtime reading is simpler and always reflects
the current code. For production at scale, a vector database (Pinecone,
pgvector, etc.) would be used to pre-embed and index documents.

### Why 20 files and 50,000 characters?

- **20 files** covers all meaningful source files in a small Spring Boot project
- **50,000 characters** fits comfortably within `gpt-5-mini`'s context window
  while leaving room for the model's response

These are constants in `AiService.java` and can be tuned without changing the
architecture.

### Why no streaming?

The current implementation returns the full answer in one HTTP response
(synchronous). Streaming (sending tokens as they arrive) would improve
perceived latency but adds complexity. It is a natural next step for a
production feature.

---

## 8. Limitations

| Limitation | Impact | How to fix |
|---|---|---|
| Max 20 files | Large codebases won't be fully covered | Increase limit or use vector search |
| 50k character cap | Long files get truncated | Chunking + vector similarity search |
| No conversation memory | Each request is independent, no follow-up context | Add session-based chat history |
| Source files read from disk | Won't work from a deployed JAR without source | Package source or use a pre-built index |
| Synchronous call | Long AI responses block the HTTP thread | Async / streaming with SSE |

---

## 9. One-Line Pitch

> **"The RAG endpoint reads the project's own Java source files at runtime,
> injects them as context into a chat prompt, and sends it to the EPAM DIAL AI
> proxy — so the model answers questions about the codebase based on actual
> code, not guesswork."**
