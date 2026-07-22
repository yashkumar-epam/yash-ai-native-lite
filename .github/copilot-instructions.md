# TaskFlow API Copilot Instructions

Follow these conventions for all changes in this project:

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

