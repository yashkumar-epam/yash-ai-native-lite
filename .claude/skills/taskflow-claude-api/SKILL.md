---
name: taskflow-claude-api
description: Use this skill when the user asks to "add an AI feature", "integrate Claude", "add LLM capabilities", "build a smart endpoint", or wants to call the Anthropic API from within the TaskFlow Spring Boot service. Loads Claude SDK patterns for Java and applies them following the project's architecture conventions.
version: 1.0.0
---

# TaskFlow — Claude API Integration Skill

This skill governs how the **`claude-api` bundled skill** is applied inside the TaskFlow Spring Boot project. It was used in Day 4 to understand Anthropic SDK patterns and model defaults for Java.

## What `claude-api` Skill Provides

The bundled `claude-api` skill (invoked via `Skill(claude-api)`) loads:
- Official Anthropic Java SDK import paths and method signatures
- Correct model IDs (`claude-opus-4-8` as default)
- Streaming patterns for long-running requests
- Tool-use / structured output examples for Java
- `thinking: {type: "adaptive"}` usage on supported models

> It is invoked automatically when Claude Code needs to write Java code that calls the Anthropic API, or when the user explicitly asks to build an LLM-powered feature.

## How to Invoke It

The skill is auto-loaded from context. To force-load it:

```
/claude-api
```

Or ask: *"Add a Claude-powered task summariser endpoint to the TaskFlow service"*

## Architecture Rules for LLM Features in TaskFlow

When adding AI-powered features, follow these project conventions on top of whatever `claude-api` provides:

### 1. Service Layer Only

All Anthropic SDK calls live in the service layer. Controllers never call the SDK directly.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskAiService {

    private final AnthropicClient anthropicClient; // injected via constructor

    @Transactional(readOnly = true)
    public String summariseTask(Long taskId) { ... }
}
```

### 2. Model Default

Use `claude-opus-4-8` unless the user names a different model:

```java
.model("claude-opus-4-8")
```

### 3. Streaming for Long Responses

Any endpoint that returns generated text should stream:

```java
anthropicClient.messages().stream(MessageStreamParams.builder()
    .model("claude-opus-4-8")
    .maxTokens(1024)
    .addUserMessage(prompt)
    .build())
```

### 4. DTO Wrapping

AI responses are always wrapped in a response DTO — never return raw API objects from controllers:

```java
@Data @Builder
public class TaskSummaryResponseDTO {
    private Long taskId;
    private String summary;
    private String model;
}
```

### 5. Error Handling

All `AnthropicException` instances must be caught in `GlobalExceptionHandler`, not in individual services.

## Maven Dependency

```xml
<dependency>
    <groupId>com.anthropic</groupId>
    <artifactId>anthropic-java</artifactId>
    <version><!-- check latest on Maven Central --></version>
</dependency>
```

## Day 4 Context

The `claude-api` skill was listed as an allowed skill in `.claude/settings.local.json`:

```json
"Skill(claude-api)"
```

It was loaded to ensure any LLM-related code written during Day 4 followed the correct Anthropic Java SDK patterns — specifically to avoid stale API shapes (e.g., `budget_tokens` which is deprecated on Opus 4.6+).
