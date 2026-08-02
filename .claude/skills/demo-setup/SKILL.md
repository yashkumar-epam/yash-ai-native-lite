---
name: demo-setup
description: Use this skill when the user says "/demo-setup", "prepare the demo", "set up for the demo", or "get ready for the management presentation". Verifies the SupportIQ app is running, confirms demo data is seeded, prints the complete demo walkthrough script with exact API calls.
argument-hint: [base-url]
allowed-tools: [Bash, Read]
version: 1.0.0
---

# SupportIQ Demo Setup

Verify everything is ready for the SupportIQ management demo and print the complete walkthrough script.

## Step 1 — Determine Base URL

Use `$ARGUMENTS` if provided (e.g., `http://localhost:8080`). Default: `http://localhost:8080`.

## Step 2 — Health Check

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/support/tickets
```

If not 200: print "App is not running. Start it with: mvn spring-boot:run" and stop.

## Step 3 — Verify Demo Data

```bash
curl -s http://localhost:8080/api/support/tickets | python -c "import sys,json; t=json.load(sys.stdin); print(f'Tickets seeded: {len(t)}')"
```

If fewer than 15 tickets: "Demo data not seeded correctly. Restart the application."

## Step 4 — Print Demo Script

Output the following complete demo walkthrough:

---

# SupportIQ — Management Demo Script

**Setup:** App running at http://localhost:8080 | Swagger UI: http://localhost:8080/swagger-ui.html

---

## ACT 1: The Problem (30 sec)

**Say:** "Your support team receives hundreds of emails daily. Without AI, every agent reads, classifies, and prioritizes manually. Here's what that queue looks like."

```
GET http://localhost:8080/api/support/tickets
```
Show the list — 20 tickets, raw data, no intelligence.

---

## ACT 2: AI Intelligence Overview (1 min)

**Say:** "Now watch what happens when we ask AI to assess the entire queue."

```
GET http://localhost:8080/api/support/dashboard
```

**Point out:**
- `queueHealthScore` — is the team keeping up?
- `queueStatus` — HEALTHY / AT_RISK / CRITICAL
- `aiRecommendations` — specific, actionable advice
- `escalationAlert` — immediate flag if something critical is open

**Say:** "This used to take a support manager 30 minutes of spreadsheet work. Now it's one API call — 2 seconds."

---

## ACT 3: The Hero — Analyze Any Email (2 min)

**Say:** "Here's the most powerful feature. Watch me paste a raw customer email — no ticket, no routing, just text."

```
POST http://localhost:8080/api/support/ai/analyze
Content-Type: application/json

{
  "rawText": "I am absolutely furious. Your system charged my corporate credit card TWICE for the annual plan — $2,400 was debited this morning. This is completely unacceptable. I am ready to dispute this with my bank and report this to the consumer protection agency. If this is not resolved TODAY I will be cancelling all 50 seats of our enterprise subscription."
}
```

**Point out in response:**
- `category`: BILLING — auto-classified
- `sentimentScore`: 1/10 — VERY_ANGRY
- `riskScore`: 9/10 — about to churn
- `escalationRequired`: true — manager must see this
- `keyIssues` — AI extracted the specific problems
- `estimatedResolutionTime` — sets expectation with the customer
- `urgencyFactors` — "50-seat enterprise", "bank dispute threat", "same-day deadline"

---

## ACT 4: AI Drafts the Reply (1.5 min)

**Say:** "The agent now needs to respond. Instead of writing from scratch..."

```
POST http://localhost:8080/api/support/tickets/1/draft-reply
```

**Point out:**
- `tone`: APOLOGETIC — AI chose this based on the ticket context
- `greeting` / `body` / `closing` — structured, ready to send
- `includesRefundOffer` — AI knows whether to offer compensation
- `keyPointsAddressed` — checklist that the agent verifies

**Say:** "Agent review time: 60 seconds. Handle time drops from 8 minutes to 90 seconds. Every reply sounds like your best agent wrote it."

---

## ACT 5: Analyze & Escalate Existing Ticket (1 min)

**Say:** "For stored tickets, we can run deep analysis and automatically update the ticket with AI insights."

```
POST http://localhost:8080/api/support/tickets/2/analyze
```

**Point out:** Ticket status changes to ESCALATED automatically. Sentiment, category, and priority are all written back to the database.

---

## ACT 6: Bulk Triage — Morning Queue (1 min)

**Say:** "Every morning, the team needs to know what to work on first. Instead of gut instinct..."

```
POST http://localhost:8080/api/support/tickets/bulk-triage
Content-Type: application/json

{
  "ticketIds": [1, 3, 5, 6, 7, 10, 17, 18]
}
```

**Point out:**
- `urgencyRank` — 1 is most critical
- `urgencyReason` — specific explanation for each ranking
- `overallInsight` — AI's summary of the queue state

---

## ACT 7: Escalation Queue (30 sec)

```
GET http://localhost:8080/api/support/tickets/escalation-queue
```

**Say:** "This is the manager's emergency lane. Every ticket flagged for escalation, in one filtered view. Nothing falls through the cracks."

---

## Key Business Takeaways

| What AI Does | Business Impact |
|---|---|
| Classifies every ticket instantly | Routing accuracy: 95%+ vs 78% manual |
| Sentiment + risk scoring | Catch churn risk before the customer leaves |
| Drafts professional replies | Handle time: 8min → 90sec |
| Bulk triage at scale | Morning queue review: 15min → instant |
| Live dashboard | Manager visibility: real-time, not end-of-day |

---

## Swagger UI Walkthrough (alternative)

Open: **http://localhost:8080/swagger-ui.html**
- Navigate to "SupportIQ — AI Intelligence" tag
- Use "Try it out" for interactive demos
- The ticket IDs in the demo data start at 1

---

Demo ready. Good luck!
