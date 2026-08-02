export const meta = {
  name: 'demo-prep',
  description: 'SupportIQ demo preparation: verifies all AI endpoints, runs sample calls against demo data, generates demo talking points, and produces a complete management presentation guide',
  phases: [
    { title: 'Verify', detail: 'Check app health and demo data seeding' },
    { title: 'Test Endpoints', detail: 'Run sample calls on all 5 AI endpoints with demo tickets' },
    { title: 'Generate Script', detail: 'AI writes personalized demo talking points from actual response data' },
  ],
}

// Usage: Workflow({ name: 'demo-prep' })
// Optional: Workflow({ name: 'demo-prep', args: { baseUrl: 'http://localhost:8080' } })

const BASE_URL = (args && args.baseUrl) || 'http://localhost:8080'

const ENDPOINT_STATUS_SCHEMA = {
  type: 'object',
  properties: {
    endpoint: { type: 'string' },
    status: { type: 'string', enum: ['OK', 'FAIL', 'SKIP'] },
    statusCode: { type: 'number' },
    keyDataPoints: { type: 'array', items: { type: 'string' } },
    demoTalkingPoint: { type: 'string' },
    issue: { type: 'string' },
  },
  required: ['endpoint', 'status'],
}

const DEMO_SCRIPT_SCHEMA = {
  type: 'object',
  properties: {
    openingHook: { type: 'string' },
    endpointScripts: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          endpoint: { type: 'string' },
          sayThis: { type: 'string' },
          pointOutInResponse: { type: 'array', items: { type: 'string' } },
          businessPunchline: { type: 'string' },
        },
      },
    },
    closingStatement: { type: 'string' },
    anticipatedQuestions: {
      type: 'array',
      items: {
        type: 'object',
        properties: { question: { type: 'string' }, answer: { type: 'string' } },
      },
    },
  },
  required: ['openingHook', 'endpointScripts', 'closingStatement'],
}

// ── Phase 1: Verify ──────────────────────────────────────────────────────────
phase('Verify')

const healthCheck = await agent(
  `Verify the SupportIQ demo environment is ready. Run these checks:

1. GET ${BASE_URL}/api/support/tickets
   - Should return HTTP 200 with at least 15 tickets
   - Report: ticket count and status breakdown

2. GET ${BASE_URL}/api/support/tickets/escalation-queue
   - Should return tickets with escalationRequired=true
   - Report: count of escalated tickets

3. Check that tickets include the critical demo scenarios:
   - A BILLING ticket with high anger (sentimentScore <= 2)
   - An ACCOUNT ticket flagged for escalation
   - A TECHNICAL ticket with HIGH priority

Return a health summary. If any check fails, explain exactly what's missing.`,
  { label: 'health-check', phase: 'Verify' }
)

log('Health check complete — testing all AI endpoints')

// ── Phase 2: Test Endpoints (5 parallel) ─────────────────────────────────────
phase('Test Endpoints')

const ENDPOINT_TESTS = [
  {
    label: 'test-dashboard',
    prompt: `Test the SupportIQ AI dashboard endpoint.

Call: GET ${BASE_URL}/api/support/dashboard

Report what you get back. Extract these specific values:
- queueHealthScore (number)
- queueStatus (HEALTHY/AT_RISK/CRITICAL)
- openCount, escalatedCount
- top aiRecommendation (first one)
- escalationAlert if present

If the call fails, report the HTTP status code and error message.
Endpoint: "GET /api/support/dashboard"`,
  },
  {
    label: 'test-raw-analyze',
    prompt: `Test the SupportIQ raw text analysis endpoint — the HERO endpoint.

Call: POST ${BASE_URL}/api/support/ai/analyze
Body:
{
  "rawText": "I am absolutely furious. Your system charged my corporate credit card TWICE for the annual plan — $2,400 was debited this morning. This is completely unacceptable. I am ready to dispute this with my bank and report this to the consumer protection agency. If this is not resolved TODAY I will be cancelling all 50 seats of our enterprise subscription."
}

Report:
- HTTP status code
- category detected
- sentimentScore (should be 1 or 2)
- sentimentLabel (should be VERY_ANGRY)
- riskScore (should be 8-10)
- escalationRequired (should be true)
- top urgencyFactor

Endpoint: "POST /api/support/ai/analyze"`,
  },
  {
    label: 'test-ticket-analyze',
    prompt: `Test the SupportIQ ticket analysis endpoint.

First, call GET ${BASE_URL}/api/support/tickets to find the first ticket with status OPEN.
Then call POST ${BASE_URL}/api/support/tickets/{id}/analyze using that ticket's ID.

Report:
- HTTP status code
- Which ticket was analyzed (ticketNumber)
- category result
- sentimentScore result
- escalationRequired result
- Any interesting findings in keyIssues

Endpoint: "POST /api/support/tickets/{id}/analyze"`,
  },
  {
    label: 'test-draft-reply',
    prompt: `Test the SupportIQ draft reply endpoint.

Call GET ${BASE_URL}/api/support/tickets to find a ticket that is either:
- A BILLING ticket, OR
- Has sentimentLabel ANGRY or VERY_ANGRY

Then call POST ${BASE_URL}/api/support/tickets/{id}/draft-reply

Report:
- HTTP status code
- tone selected (APOLOGETIC/EMPATHETIC/INFORMATIONAL/PROFESSIONAL)
- includesRefundOffer (true/false)
- first 100 characters of the body
- keyPointsAddressed (first 2)

Endpoint: "POST /api/support/tickets/{id}/draft-reply"`,
  },
  {
    label: 'test-bulk-triage',
    prompt: `Test the SupportIQ bulk triage endpoint.

Call GET ${BASE_URL}/api/support/tickets to get all tickets.
Pick 6 tickets with varied statuses and priorities.
Then call POST ${BASE_URL}/api/support/tickets/bulk-triage with those IDs.

Report:
- HTTP status code
- totalTriaged count
- escalationCount
- Ticket ranked #1 (most urgent): ticketNumber and urgencyReason
- overallInsight (first 100 chars)

Endpoint: "POST /api/support/tickets/bulk-triage"`,
  },
]

const endpointResults = await parallel(
  ENDPOINT_TESTS.map(t => () => agent(t.prompt, {
    label: t.label,
    phase: 'Test Endpoints',
    schema: ENDPOINT_STATUS_SCHEMA,
  }))
)

const results = endpointResults.filter(Boolean)
const allPassed = results.every(r => r.status === 'OK')
const failed = results.filter(r => r.status === 'FAIL')

log(`Endpoint tests complete: ${results.filter(r => r.status === 'OK').length}/${ENDPOINT_TESTS.length} passed`)

if (failed.length > 0) {
  log(`FAILING: ${failed.map(r => r.endpoint + ': ' + (r.issue || 'unknown error')).join(', ')}`)
}

// ── Phase 3: Generate Demo Script ─────────────────────────────────────────────
phase('Generate Script')

const demoScript = await agent(
  `You are a world-class technical demo coach. Based on these actual API test results, write a compelling demo script for presenting SupportIQ to senior management (non-technical audience).

ENDPOINT TEST RESULTS:
${results.map(r => `
${r.endpoint}: ${r.status}
Key data points: ${(r.keyDataPoints || []).join(', ')}
Demo talking point: ${r.demoTalkingPoint || 'not provided'}
`).join('\n')}

HEALTH CHECK SUMMARY:
${healthCheck}

Write a complete, ready-to-deliver demo script that:
1. Opens with a compelling business problem hook (NOT "let me show you the technology")
2. For each endpoint: exact words to say, what to highlight in the response, and a business punchline
3. Uses ACTUAL numbers from the test results (real sentiment scores, real queue health score, etc.)
4. Closes with a memorable ROI statement
5. Anticipates the top 3 management questions (cost, accuracy, privacy)`,
  {
    label: 'generate-demo-script',
    phase: 'Generate Script',
    schema: DEMO_SCRIPT_SCHEMA,
    agentType: 'demo-guide-agent',
  }
)

return {
  ready: allPassed,
  healthCheck,
  endpointStatus: results.map(r => ({ endpoint: r.endpoint, status: r.status, issue: r.issue })),
  failedEndpoints: failed.map(r => r.endpoint),
  demoScript,
  quickReference: {
    baseUrl: BASE_URL,
    swaggerUrl: `${BASE_URL}/swagger-ui.html`,
    heroEndpoint: `POST ${BASE_URL}/api/support/ai/analyze`,
    dashboardEndpoint: `GET ${BASE_URL}/api/support/dashboard`,
  },
}
