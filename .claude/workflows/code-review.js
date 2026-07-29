export const meta = {
  name: 'code-review',
  description: 'Multi-dimensional code review: 4 parallel specialist agents check conventions, validation, tests, and API design then synthesize findings',
  phases: [
    { title: 'Gather', detail: 'Collect changed files and diffs' },
    { title: 'Review', detail: '4 parallel agents: conventions, validation, tests, API design' },
    { title: 'Synthesize', detail: 'Merge findings, deduplicate, rank by severity' },
  ],
}

// Usage: Workflow({ name: 'code-review' })
// Optional: Workflow({ name: 'code-review', args: { branch: 'main' } })

const BASE_BRANCH = (args && args.branch) || 'main'

const FINDING_SCHEMA = {
  type: 'object',
  properties: {
    dimension: { type: 'string' },
    passed: { type: 'boolean' },
    findings: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          severity: { type: 'string', enum: ['HIGH', 'MEDIUM', 'LOW'] },
          file: { type: 'string' },
          location: { type: 'string' },
          issue: { type: 'string' },
          fix: { type: 'string' },
        },
        required: ['severity', 'issue', 'fix'],
      },
    },
  },
  required: ['dimension', 'passed', 'findings'],
}

// ── Phase 1: Gather ───────────────────────────────────────────────────────────
phase('Gather')

const changedFiles = await agent(
  `Run these commands and return the output:
1. git diff ${BASE_BRANCH}...HEAD --name-only --diff-filter=AM
2. git diff ${BASE_BRANCH}...HEAD -- '*.java' (full diff, max 200 lines per file)

Return: list of changed Java files and a summary of what changed in each.`,
  { label: 'gather-changes' }
)

log(`Changed files gathered`)

// ── Phase 2: Review (4 parallel dimensions) ───────────────────────────────────
phase('Review')

const DIMENSIONS = [
  {
    key: 'conventions',
    label: 'review-conventions',
    prompt: `Review these TaskFlow changes for Spring Boot convention violations:

${changedFiles}

Check every changed file for:
- @Autowired field injection (must use constructor injection)
- Missing @Transactional on write methods (create/update/delete)
- Missing @Slf4j on classes
- Entity returned from controller (only DTOs allowed)
- Missing @Valid on @RequestBody parameters
- Exceptions caught in service methods (must go through GlobalExceptionHandler)

Dimension: "Spring Boot Conventions"`,
  },
  {
    key: 'validation',
    label: 'review-validation',
    prompt: `Review these TaskFlow changes for validation correctness:

${changedFiles}

Check every changed DTO and controller for:
- All user-facing fields have constraints (@NotBlank, @Size, @Pattern, @FutureOrPresent)
- Validation messages match EXACTLY: "Title is required", "Title cannot exceed 255 characters", "Description cannot exceed 500 characters", "Status must be one of: TODO, IN_PROGRESS, DONE", "Priority must be one of: LOW, MEDIUM, HIGH", "Due date must be today or in the future"
- @Valid present on controller @RequestBody parameters
- No custom validation bypassing global handler

Dimension: "Input Validation"`,
  },
  {
    key: 'tests',
    label: 'review-tests',
    prompt: `Review these TaskFlow changes for test coverage:

${changedFiles}

For every changed service method or controller endpoint:
- Corresponding test exists and covers the new code
- @MockBean used (not @Mock) in @WebMvcTest classes
- Happy path AND error path tested
- ResourceNotFoundException tested for findById calls
- New DTO fields included in builder calls in tests
- Validation rejection cases have tests

Dimension: "Test Coverage"`,
  },
  {
    key: 'api-design',
    label: 'review-api-design',
    prompt: `Review these TaskFlow changes for REST API design quality:

${changedFiles}

Check:
- HTTP status codes (POST→201, DELETE→204, GET/PUT→200)
- Plural noun paths (/api/tasks not /api/task)
- @Operation and @ApiResponses on all public endpoints
- No breaking change to existing endpoint signatures
- Paginated endpoints return PagedResponseDTO (not Page<T>)
- Response DTOs have all new fields the request DTO accepts

Dimension: "API Design"`,
  },
]

const reviews = await parallel(
  DIMENSIONS.map(d => () => agent(d.prompt, {
    label: d.label,
    phase: 'Review',
    schema: FINDING_SCHEMA,
    agentType: 'taskflow-reviewer',
  }))
)

// ── Phase 3: Synthesize ───────────────────────────────────────────────────────
phase('Synthesize')

const validReviews = reviews.filter(Boolean)
const allFindings = validReviews.flatMap(r => r.findings || [])

const high = allFindings.filter(f => f.severity === 'HIGH')
const medium = allFindings.filter(f => f.severity === 'MEDIUM')
const low = allFindings.filter(f => f.severity === 'LOW')

log(`Total: ${allFindings.length} findings — HIGH:${high.length} MEDIUM:${medium.length} LOW:${low.length}`)

const summary = await agent(
  `Synthesize these code review findings into a final report.

Findings by dimension:
${validReviews.map(r => `${r.dimension} (${r.passed ? 'PASS' : 'FAIL'}): ${r.findings.length} findings`).join('\n')}

HIGH severity findings (must fix before merge):
${high.map(f => `- [${f.file || 'unknown'}] ${f.issue} → Fix: ${f.fix}`).join('\n') || 'None'}

MEDIUM severity findings:
${medium.map(f => `- [${f.file || 'unknown'}] ${f.issue}`).join('\n') || 'None'}

Write a concise executive summary (5-8 lines) stating:
1. Overall verdict (APPROVED / NEEDS CHANGES)
2. What looks good
3. What must be fixed before merge (HIGH findings only)
4. Estimated effort to fix issues`,
  { label: 'synthesize' }
)

return {
  verdict: high.length === 0 ? 'APPROVED' : 'NEEDS CHANGES',
  totalFindings: allFindings.length,
  high,
  medium,
  low,
  dimensionResults: validReviews.map(r => ({ dimension: r.dimension, passed: r.passed, count: r.findings.length })),
  summary,
}
