---
name: support-briefing
description: Use this skill when the user says "/support-briefing", "give me a support summary", "how is the support queue", "brief me on support", or "management summary of support". Fetches live queue data and formats a concise executive briefing suitable for presenting to management.
argument-hint: [base-url]
allowed-tools: [Bash, Read]
version: 1.0.0
---

# Support Briefing

Generate an executive-level support queue briefing from live SupportIQ data.

## Step 1 — Fetch Live Data

Call these endpoints in parallel (or sequentially if bash):

```bash
BASE=${ARGUMENTS:-http://localhost:8080}

echo "=== DASHBOARD ===" && curl -s $BASE/api/support/dashboard
echo ""
echo "=== ESCALATION QUEUE ===" && curl -s $BASE/api/support/tickets/escalation-queue
echo ""
echo "=== OPEN TICKETS ===" && curl -s "$BASE/api/support/tickets?status=OPEN"
```

## Step 2 — Format Executive Briefing

Parse the JSON responses and present this briefing format:

---

## Support Queue Briefing — [today's date]

### Queue Health
**Score:** [queueHealthScore]/100 — [queueStatus]

### Ticket Summary
| Status | Count |
|---|---|
| Open | [openCount] |
| In Progress | [inProgressCount] |
| Escalated | [escalatedCount] |
| Resolved | [resolvedCount] |
| Closed | [closedCount] |
| **Total** | **[totalTickets]** |

### Sentiment Overview
- Average sentiment: [averageSentimentScore]/10 ([averageSentimentLabel])
- Critical priority tickets: [criticalCount]
- High priority tickets: [highPriorityCount]

### Category Breakdown
[Format categoryBreakdown map as a table]

### Escalation Alert
[If escalationAlert is non-null: show in bold red language]
[List escalation queue tickets with customer names and subjects]

### AI Recommendations
[List each recommendation from aiRecommendations as a bullet]

### Top Issues
[List each item from topIssues as a numbered list]

---

## Step 3 — Add Action Items

Based on the data, append:

**Immediate Actions Required:**
1. [Any tickets with escalationRequired=true and status=OPEN]
2. [Any CRITICAL priority tickets not yet IN_PROGRESS]
3. [Any tickets with sentimentScore ≤ 2]

If everything looks healthy, say: "No immediate escalations. Queue is healthy."

## Step 4 — One-Sentence Summary

End with: "**Bottom line:** [one sentence appropriate for reading aloud in a management meeting]"
