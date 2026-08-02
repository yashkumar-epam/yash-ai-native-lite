export const meta = {
  name: 'support-ai-insights',
  description: 'SupportIQ comprehensive intelligence: 4 parallel agents analyze queue health, escalation risks, sentiment trends, and generate management briefing with concrete recommendations',
  phases: [
    { title: 'Gather', detail: 'Fetch all ticket data from SupportIQ API' },
    { title: 'Analyze', detail: '4 parallel AI agents: queue health, risk scan, sentiment trends, category patterns' },
    { title: 'Synthesize', detail: 'Merge findings into executive briefing with prioritized action items' },
  ],
}

// Usage: Workflow({ name: 'support-ai-insights' })
// Optional: Workflow({ name: 'support-ai-insights', args: { baseUrl: 'http://localhost:8080' } })

const BASE_URL = (args && args.baseUrl) || 'http://localhost:8080'

const INSIGHT_SCHEMA = {
  type: 'object',
  properties: {
    dimension: { type: 'string' },
    score: { type: 'number' },
    findings: { type: 'array', items: { type: 'string' } },
    recommendations: { type: 'array', items: { type: 'string' } },
    urgentActions: { type: 'array', items: { type: 'string' } },
  },
  required: ['dimension', 'score', 'findings', 'recommendations'],
}

const BRIEFING_SCHEMA = {
  type: 'object',
  properties: {
    executiveSummary: { type: 'string' },
    overallHealthScore: { type: 'number' },
    overallStatus: { type: 'string' },
    topPriorityActions: { type: 'array', items: { type: 'string' } },
    businessRisks: { type: 'array', items: { type: 'string' } },
    wins: { type: 'array', items: { type: 'string' } },
    recommendedNextSteps: { type: 'array', items: { type: 'string' } },
  },
  required: ['executiveSummary', 'overallHealthScore', 'overallStatus', 'topPriorityActions'],
}

// ── Phase 1: Gather ──────────────────────────────────────────────────────────
phase('Gather')

const ticketData = await agent(
  `Fetch and summarize the current SupportIQ ticket queue by calling these REST endpoints:

  1. GET ${BASE_URL}/api/support/tickets — all tickets
  2. GET ${BASE_URL}/api/support/tickets/escalation-queue — escalated tickets
  3. GET ${BASE_URL}/api/support/dashboard — AI dashboard (may fail if AI service unavailable)

  Return a structured summary with:
  - Total ticket count by status (OPEN, IN_PROGRESS, ESCALATED, RESOLVED, CLOSED)
  - Ticket count by priority (CRITICAL, HIGH, MEDIUM, LOW)
  - Ticket count by category (BILLING, TECHNICAL, ACCOUNT, COMPLAINT, REFUND, GENERAL)
  - List of all CRITICAL/HIGH priority open tickets (id, ticketNumber, subject, sentimentLabel)
  - List of all escalation-required tickets
  - Average sentiment score across all tickets with sentimentScore set
  - Any tickets with sentimentScore <= 2 (VERY_ANGRY customers)
  - Dashboard queueHealthScore and queueStatus if available`,
  { label: 'gather-ticket-data', phase: 'Gather' }
)

log('Ticket data gathered — starting 4-dimension analysis')

// ── Phase 2: Analyze (4 parallel dimensions) ──────────────────────────────────
phase('Analyze')

const DIMENSIONS = [
  {
    label: 'analyze-queue-health',
    prompt: `You are a customer support operations analyst. Based on this SupportIQ queue data, evaluate QUEUE HEALTH.

Queue data:
${ticketData}

Analyze:
1. Are open tickets being resolved fast enough? (open vs resolved ratio)
2. Is the escalation rate acceptable? (<5% healthy, 5-15% at risk, >15% critical)
3. Are CRITICAL and HIGH priority tickets being actively worked? (IN_PROGRESS)
4. Is the IN_PROGRESS count appropriate relative to queue size?

Dimension: "Queue Health & Operations"
Score 0-100 (100 = excellent operations, everything flowing well)`,
  },
  {
    label: 'analyze-escalation-risk',
    prompt: `You are a customer churn and escalation risk analyst. Based on this SupportIQ data, identify RISK FACTORS.

Queue data:
${ticketData}

Analyze:
1. Which customers are highest churn risk based on sentiment + priority?
2. Are there any legal/regulatory threats in open tickets?
3. Which escalated tickets need IMMEDIATE manager attention?
4. Is there a pattern suggesting systemic issues (multiple tickets, same problem)?
5. What's the financial/business risk if these tickets aren't resolved soon?

Dimension: "Escalation & Churn Risk"
Score 0-100 (100 = no risk, 0 = critical business risk)`,
  },
  {
    label: 'analyze-sentiment-trends',
    prompt: `You are a customer experience analyst. Based on this SupportIQ data, analyze SENTIMENT TRENDS.

Queue data:
${ticketData}

Analyze:
1. What is the emotional state of the customer base right now?
2. Are there more angry (1-4) or satisfied (7-10) customers?
3. Which issue categories produce the most negative sentiment?
4. Are there any bright spots — highly satisfied customers worth noting?
5. What does the sentiment distribution predict about upcoming NPS/reviews?

Dimension: "Customer Sentiment & Experience"
Score 0-100 (100 = customers are very happy, 0 = mass dissatisfaction)`,
  },
  {
    label: 'analyze-category-patterns',
    prompt: `You are a support operations strategist. Based on this SupportIQ data, identify SYSTEMIC PATTERNS.

Queue data:
${ticketData}

Analyze:
1. What are the top issue categories? Are these expected or surprising?
2. Is there a dominant problem type that suggests a product/process issue?
3. Are technical issues trending (could indicate a recent release bug)?
4. Billing issues — could these be addressed with better self-service?
5. What process improvements or proactive outreach would reduce ticket volume?

Dimension: "Issue Patterns & Root Cause"
Score 0-100 (100 = patterns are normal and well-understood, 0 = systemic crisis)`,
  },
]

const analyses = await parallel(
  DIMENSIONS.map(d => () => agent(d.prompt, {
    label: d.label,
    phase: 'Analyze',
    schema: INSIGHT_SCHEMA,
  }))
)

// ── Phase 3: Synthesize ──────────────────────────────────────────────────────
phase('Synthesize')

const validAnalyses = analyses.filter(Boolean)
const allRecommendations = validAnalyses.flatMap(a => a.recommendations || [])
const allUrgentActions = validAnalyses.flatMap(a => a.urgentActions || [])
const avgScore = validAnalyses.length
  ? Math.round(validAnalyses.reduce((s, a) => s + (a.score || 50), 0) / validAnalyses.length)
  : 50

log(`Analysis complete — avg health score: ${avgScore}. Generating executive briefing...`)

const briefing = await agent(
  `You are a VP of Customer Success preparing a management briefing. Based on these 4 analysis dimensions, write a concise executive briefing.

DIMENSION SCORES:
${validAnalyses.map(a => `${a.dimension}: ${a.score}/100`).join('\n')}

KEY FINDINGS:
${validAnalyses.map(a => `${a.dimension}:\n${(a.findings || []).join('\n')}`).join('\n\n')}

RECOMMENDATIONS (all dimensions combined):
${allRecommendations.join('\n')}

URGENT ACTIONS NEEDED:
${allUrgentActions.join('\n') || 'None identified'}

Write an executive briefing that a VP could read in 2 minutes. Be specific and data-driven.`,
  {
    label: 'executive-briefing',
    phase: 'Synthesize',
    schema: BRIEFING_SCHEMA,
  }
)

return {
  overallHealthScore: avgScore,
  dimensionScores: validAnalyses.map(a => ({ dimension: a.dimension, score: a.score })),
  briefing,
  urgentActions: allUrgentActions,
  allRecommendations,
  rawAnalyses: validAnalyses,
}
