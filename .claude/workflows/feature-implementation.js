export const meta = {
  name: 'feature-implementation',
  description: 'Multi-agent feature implementation: read GitHub issue via MCP → parallel layer implementation → tests → review',
  phases: [
    { title: 'Research', detail: 'Read GitHub issue via MCP + current source files' },
    { title: 'Plan', detail: 'Architect determines exact changes per file' },
    { title: 'Implement', detail: 'Parallel agents: entity, request DTO, response DTO' },
    { title: 'Integrate', detail: 'Mapper + repository (depend on DTOs)' },
    { title: 'Test', detail: 'Update existing tests + add new test cases' },
    { title: 'Review', detail: 'Verify every acceptance criterion is met' },
  ],
}

// Usage: Workflow({ name: 'feature-implementation', args: { issueNumber: 3 } })
const REPO = 'yashkumar-epam/yash-ai-native-lite'
const ISSUE_NUMBER = args && args.issueNumber

if (!ISSUE_NUMBER) {
  log('ERROR: args.issueNumber is required. Example: { issueNumber: 3 }')
}

const ISSUE_SCHEMA = {
  type: 'object',
  properties: {
    title: { type: 'string' },
    requirements: { type: 'array', items: { type: 'string' } },
    acceptanceCriteria: { type: 'array', items: { type: 'string' } },
  },
  required: ['title', 'requirements', 'acceptanceCriteria'],
}

const PLAN_SCHEMA = {
  type: 'object',
  properties: {
    entityChanges: { type: 'array', items: { type: 'string' } },
    requestDtoChanges: { type: 'array', items: { type: 'string' } },
    responseDtoChanges: { type: 'array', items: { type: 'string' } },
    mapperChanges: { type: 'array', items: { type: 'string' } },
    repositoryChanges: { type: 'array', items: { type: 'string' } },
    serviceChanges: { type: 'array', items: { type: 'string' } },
    testChanges: { type: 'array', items: { type: 'string' } },
  },
}

const REVIEW_SCHEMA = {
  type: 'object',
  properties: {
    allRequirementsImplemented: { type: 'boolean' },
    allCriteriaHaveTests: { type: 'boolean' },
    conventionViolations: { type: 'array', items: { type: 'string' } },
    readyToCommit: { type: 'boolean' },
  },
  required: ['readyToCommit'],
}

// ── Phase 1: Research ─────────────────────────────────────────────────────────
phase('Research')

const [issue, currentState] = await parallel([
  () => agent(
    `Use the GitHub MCP server to read issue #${ISSUE_NUMBER} from repository ${REPO}.
Extract: title, requirements (bullet points), and acceptance criteria.
Return as structured data only — no narrative.`,
    { label: 'mcp-read-issue', schema: ISSUE_SCHEMA }
  ),
  () => agent(
    `Read these TaskFlow source files and return a one-line summary of each:
- src/main/java/com/epam/taskflow/taskflow_api/model/Task.java
- src/main/java/com/epam/taskflow/taskflow_api/dto/TaskRequestDTO.java
- src/main/java/com/epam/taskflow/taskflow_api/dto/TaskResponseDTO.java
- src/main/java/com/epam/taskflow/taskflow_api/mapper/TaskMapper.java
- src/main/java/com/epam/taskflow/taskflow_api/repository/TaskRepository.java
For each: list current fields, current query methods, and any imports needed for a new field.`,
    { label: 'read-source-files' }
  ),
])

log(`Issue #${ISSUE_NUMBER}: "${issue.title}"`)
log(`Requirements: ${issue.requirements.length} | Criteria: ${issue.acceptanceCriteria.length}`)

// ── Phase 2: Plan ─────────────────────────────────────────────────────────────
phase('Plan')

const plan = await agent(
  `You are the TaskFlow architect. Based on this GitHub issue and current codebase, produce a precise implementation plan.

Issue: ${issue.title}
Requirements:
${issue.requirements.map(r => `- ${r}`).join('\n')}

Current source state:
${currentState}

Rules:
- Constructor injection, @Transactional on writes, DTOs only at controller boundary
- Validation messages must match the standards table in CLAUDE.md
- Return ONLY the list of specific changes per file — no code yet`,
  { label: 'architect-plan', schema: PLAN_SCHEMA, agentType: 'taskflow-architect' }
)

log(`Plan: entity(${plan.entityChanges && plan.entityChanges.length}), dto(${plan.requestDtoChanges && plan.requestDtoChanges.length}), mapper(${plan.mapperChanges && plan.mapperChanges.length})`)

// ── Phase 3: Implement (parallel — independent layers) ────────────────────────
phase('Implement')

await parallel([
  () => agent(
    `Implement these changes in src/main/java/com/epam/taskflow/taskflow_api/model/Task.java:
${(plan.entityChanges || []).join('\n')}
Read the file first, then apply changes with Edit tool. Add missing imports.`,
    { label: 'impl-entity', phase: 'Implement' }
  ),
  () => agent(
    `Implement these changes in src/main/java/com/epam/taskflow/taskflow_api/dto/TaskRequestDTO.java:
${(plan.requestDtoChanges || []).join('\n')}
Rules: Jakarta validation (@FutureOrPresent, @NotBlank, @Size, @Pattern). Messages must match CLAUDE.md standards table.
Read the file first, then apply changes.`,
    { label: 'impl-request-dto', phase: 'Implement' }
  ),
  () => agent(
    `Implement these changes in src/main/java/com/epam/taskflow/taskflow_api/dto/TaskResponseDTO.java:
${(plan.responseDtoChanges || []).join('\n')}
Read the file first, then apply changes.`,
    { label: 'impl-response-dto', phase: 'Implement' }
  ),
])

// ── Phase 4: Integrate (mapper + repository depend on updated DTOs) ────────────
phase('Integrate')

await parallel([
  () => agent(
    `Implement these mapper changes in src/main/java/com/epam/taskflow/taskflow_api/mapper/TaskMapper.java:
${(plan.mapperChanges || []).join('\n')}
The mapper has THREE methods: toResponseDTO, toEntity, updateEntityFromDTO.
Add new field mapping to ALL THREE. Read the file first.`,
    { label: 'impl-mapper', phase: 'Integrate' }
  ),
  () => agent(
    `Implement these repository changes in src/main/java/com/epam/taskflow/taskflow_api/repository/TaskRepository.java:
${(plan.repositoryChanges || []).join('\n')}
Add LocalDate import if needed. Read the file first.`,
    { label: 'impl-repository', phase: 'Integrate' }
  ),
])

log('Core implementation complete')

// ── Phase 5: Test ─────────────────────────────────────────────────────────────
phase('Test')

await parallel([
  () => agent(
    `Update src/test/java/com/epam/taskflow/taskflow_api/service/TaskServiceTest.java.

Changes needed:
${(plan.testChanges || []).filter(t => t.toLowerCase().includes('service')).join('\n') || 'Add new field to relevant task builders'}

For createTask and updateTask tests: include new field in ALL builder calls.
Use LocalDate.now().plusDays(7) for valid future dates.
Import java.time.LocalDate if not present.`,
    { label: 'test-service', phase: 'Test', agentType: 'taskflow-tester' }
  ),
  () => agent(
    `Update src/test/java/com/epam/taskflow/taskflow_api/controller/TaskControllerValidationTest.java.

Add validation tests for each acceptance criterion:
${issue.acceptanceCriteria.join('\n')}

Rules:
- Use @MockBean (NOT @Mock) for TaskService
- For rejection tests: .andExpect(status().isBadRequest()) + .andExpect(jsonPath("$.message", containsString("...")))
- For acceptance tests: .andExpect(status().isCreated())
- Import java.time.LocalDate
- Import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest (correct package)`,
    { label: 'test-controller', phase: 'Test', agentType: 'taskflow-tester' }
  ),
])

// ── Phase 6: Review ───────────────────────────────────────────────────────────
phase('Review')

const review = await agent(
  `Review the complete implementation of "${issue.title}" (GitHub issue #${ISSUE_NUMBER}).

Verify:
1. Every requirement was implemented: ${issue.requirements.join(' | ')}
2. Every acceptance criterion has a test: ${issue.acceptanceCriteria.join(' | ')}
3. No @Autowired anywhere
4. @MockBean (not @Mock) in controller tests
5. Validation messages match CLAUDE.md standards
6. All new imports present in changed files

Read the changed files and report honestly.`,
  { label: 'final-review', schema: REVIEW_SCHEMA, agentType: 'taskflow-reviewer' }
)

log(`Review: requirements=${review.allRequirementsImplemented}, tests=${review.allCriteriaHaveTests}, violations=${review.conventionViolations && review.conventionViolations.length}`)

return {
  issue: issue.title,
  issueNumber: ISSUE_NUMBER,
  readyToCommit: review.readyToCommit,
  violations: review.conventionViolations,
  commitMessage: `feat: ${issue.title.toLowerCase()} - closes #${ISSUE_NUMBER}`,
}
