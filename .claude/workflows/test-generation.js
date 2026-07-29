export const meta = {
  name: 'test-generation',
  description: 'Comprehensive test generation: discover coverage gaps → parallel test writers for service and controller → quality validation',
  phases: [
    { title: 'Discover', detail: 'Find untested methods and missing validation scenarios' },
    { title: 'Generate', detail: 'Parallel test writers: service tests + controller validation tests' },
    { title: 'Validate', detail: 'Check generated tests for import completeness and correctness' },
  ],
}

// Usage:
//   Workflow({ name: 'test-generation' })                          — full audit
//   Workflow({ name: 'test-generation', args: { layer: 'service' } })  — service only
//   Workflow({ name: 'test-generation', args: { layer: 'controller' } }) — controller only

const TARGET_LAYER = (args && args.layer) || 'all'

const GAP_SCHEMA = {
  type: 'object',
  properties: {
    untestedMethods: { type: 'array', items: { type: 'string' } },
    missingEdgeCases: { type: 'array', items: { type: 'string' } },
    missingValidationTests: { type: 'array', items: { type: 'string' } },
    hasCoverageGaps: { type: 'boolean' },
  },
  required: ['hasCoverageGaps'],
}

const QUALITY_SCHEMA = {
  type: 'object',
  properties: {
    serviceTestValid: { type: 'boolean' },
    controllerTestValid: { type: 'boolean' },
    missingImports: { type: 'array', items: { type: 'string' } },
    duplicateMethods: { type: 'array', items: { type: 'string' } },
    issues: { type: 'array', items: { type: 'string' } },
  },
  required: ['serviceTestValid', 'controllerTestValid'],
}

// ── Phase 1: Discover ─────────────────────────────────────────────────────────
phase('Discover')

const shouldCheckService = TARGET_LAYER === 'all' || TARGET_LAYER === 'service'
const shouldCheckController = TARGET_LAYER === 'all' || TARGET_LAYER === 'controller'

const [serviceGaps, controllerGaps] = await parallel([
  () => shouldCheckService
    ? agent(
      `Analyse TaskService test coverage gaps.

Read BOTH files:
1. src/main/java/com/epam/taskflow/taskflow_api/service/TaskService.java
2. src/test/java/com/epam/taskflow/taskflow_api/service/TaskServiceTest.java

For each public method in TaskService, check if there is a corresponding test.
Also check: are ResourceNotFoundException paths tested? Are new fields (like dueDate) included in builder calls?

Return gaps as structured data.`,
      { label: 'discover-service-gaps', schema: GAP_SCHEMA }
    )
    : Promise.resolve({ hasCoverageGaps: false, untestedMethods: [], missingEdgeCases: [] }),

  () => shouldCheckController
    ? agent(
      `Analyse TaskControllerValidationTest coverage gaps.

Read BOTH files:
1. src/main/java/com/epam/taskflow/taskflow_api/controller/TaskController.java
2. src/test/java/com/epam/taskflow/taskflow_api/controller/TaskControllerValidationTest.java

Check: is every DTO field validated with a rejection test? Are valid inputs also tested (isCreated)?
Is dueDate tested (past → 400, future → 201, null → 201)?
Is the @WebMvcTest import correct (org.springframework.boot.test.autoconfigure.web.servlet)?

Return gaps as structured data.`,
      { label: 'discover-controller-gaps', schema: GAP_SCHEMA }
    )
    : Promise.resolve({ hasCoverageGaps: false, missingValidationTests: [] }),
])

const serviceHasGaps = serviceGaps && serviceGaps.hasCoverageGaps
const controllerHasGaps = controllerGaps && controllerGaps.hasCoverageGaps

log(`Service gaps: ${serviceHasGaps ? (serviceGaps.untestedMethods && serviceGaps.untestedMethods.length) + ' untested methods' : 'none'}`)
log(`Controller gaps: ${controllerHasGaps ? (controllerGaps.missingValidationTests && controllerGaps.missingValidationTests.length) + ' missing validation tests' : 'none'}`)

if (!serviceHasGaps && !controllerHasGaps) {
  log('No coverage gaps found — nothing to generate')
  return { status: 'complete', message: 'No coverage gaps found' }
}

// ── Phase 2: Generate ─────────────────────────────────────────────────────────
phase('Generate')

await parallel([
  () => serviceHasGaps
    ? agent(
      `Add tests to TaskServiceTest for these gaps.
File: src/test/java/com/epam/taskflow/taskflow_api/service/TaskServiceTest.java

Untested methods:
${(serviceGaps.untestedMethods || []).join('\n')}

Missing edge cases:
${(serviceGaps.missingEdgeCases || []).join('\n')}

Rules:
- @ExtendWith(MockitoExtension.class) — @Mock @InjectMocks pattern
- ResourceNotFoundException tested with assertThrows + verify(mock, never())
- Include dueDate: LocalDate.now().plusDays(7) in ALL builder calls
- Add @Test @DisplayName on every new method
- Append to end of class before closing }`,
      { label: 'gen-service-tests', phase: 'Generate', agentType: 'taskflow-tester' }
    )
    : agent('Service tests are complete — no generation needed.', { label: 'gen-service-skip', phase: 'Generate' }),

  () => controllerHasGaps
    ? agent(
      `Add validation tests to TaskControllerValidationTest for these gaps.
File: src/test/java/com/epam/taskflow/taskflow_api/controller/TaskControllerValidationTest.java

Missing validation tests:
${(controllerGaps.missingValidationTests || []).join('\n')}

Rules:
- @MockBean TaskService (never @Mock)
- @WebMvcTest import: org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
- Rejection tests: status().isBadRequest() + jsonPath("$.message", containsString("..."))
- Acceptance tests: status().isCreated()
- Valid dueDate = LocalDate.now().plusDays(7) serialised as "YYYY-MM-DD" string
- @Test @DisplayName on every new method
- Append to end of class before closing }`,
      { label: 'gen-controller-tests', phase: 'Generate', agentType: 'taskflow-tester' }
    )
    : agent('Controller tests are complete — no generation needed.', { label: 'gen-controller-skip', phase: 'Generate' }),
])

// ── Phase 3: Validate ─────────────────────────────────────────────────────────
phase('Validate')

const quality = await agent(
  `Validate both test files for correctness after recent edits.

Read:
- src/test/java/com/epam/taskflow/taskflow_api/service/TaskServiceTest.java
- src/test/java/com/epam/taskflow/taskflow_api/controller/TaskControllerValidationTest.java

Check:
1. All necessary imports are present (java.time.LocalDate, MockBean, WebMvcTest correct package, etc.)
2. No duplicate @Test method names
3. @MockBean used (not @Mock) in the controller test
4. @WebMvcTest imported from org.springframework.boot.test.autoconfigure.web.servlet
5. Every @Test method has a @DisplayName
6. No incomplete stubs (no TODO, no throw new UnsupportedOperationException)

Report any issues found.`,
  { label: 'validate-quality', schema: QUALITY_SCHEMA }
)

log(`Validation: service=${quality.serviceTestValid}, controller=${quality.controllerTestValid}`)
if (quality.issues && quality.issues.length > 0) {
  log(`Issues found: ${quality.issues.join('; ')}`)
}

return {
  serviceGaps: serviceGaps && serviceGaps.untestedMethods,
  controllerGaps: controllerGaps && controllerGaps.missingValidationTests,
  testsGenerated: true,
  quality: {
    serviceValid: quality.serviceTestValid,
    controllerValid: quality.controllerTestValid,
    issues: quality.issues,
  },
}
