---
name: triage-queue
description: Use this skill when the user says "/triage-queue", "triage the queue", "prioritize open tickets", "what should we work on first", or "rank the tickets". Fetches all open/escalated tickets and runs AI bulk triage to produce a ranked work queue for the support team.
argument-hint: [ticket-ids or "all"]
allowed-tools: [Bash, Read]
version: 1.0.0
---

# Triage Queue

Fetch open support tickets and run AI bulk triage to rank them by urgency.

## Step 1 — Fetch Open Tickets

```bash
BASE=http://localhost:8080

# Get all open tickets
OPEN=$(curl -s "$BASE/api/support/tickets?status=OPEN")
# Get escalated tickets
ESCALATED=$(curl -s "$BASE/api/support/tickets?status=ESCALATED")
```

## Step 2 — Extract Ticket IDs

If `$ARGUMENTS` contains specific IDs (comma-separated or "all"), use those.
Otherwise, extract IDs from the fetched open + escalated tickets.

Collect up to 50 ticket IDs total (API limit).

## Step 3 — Run Bulk Triage

If 2+ tickets are found:

```bash
# Construct JSON body with ticket IDs
# Then call bulk-triage
curl -s -X POST "$BASE/api/support/tickets/bulk-triage" \
  -H "Content-Type: application/json" \
  -d '{"ticketIds": [/* IDs here */]}'
```

## Step 4 — Format Triage Report

Present the results as:

---

## Triage Report — [timestamp]

**AI Model:** [model]
**Tickets analyzed:** [totalTriaged]
**Escalations flagged:** [escalationCount]

### Ranked Work Queue

| Rank | Ticket | Customer Issue | Suggested Priority | Escalate? | Why |
|---|---|---|---|---|---|
| 1 | TKT-XXXX | ... | CRITICAL | YES | [urgencyReason] |
| 2 | ... | | | | |

### Overall Insight
> [overallInsight from AI]

### Escalation Actions Needed
[List only tickets where escalationRequired=true that aren't already ESCALATED status]
For each: "Call `PATCH /api/support/tickets/{id}/status` with `{"status": "ESCALATED"}`"

---

## Step 5 — Next Steps

Print the recommended actions:
1. **Assign top 3 tickets** to available agents (suggest `PATCH /{id}/status` calls)
2. **Draft replies** for top 2 — "Run `POST /api/support/tickets/{id}/draft-reply` for tickets #1 and #2"
3. **Escalation** — "Alert manager to [list escalation tickets]"

If fewer than 2 open tickets found: "Queue is clear! No triage needed."
