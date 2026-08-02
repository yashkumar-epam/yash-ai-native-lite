# SupportIQ — Management Briefing & Demo Dry Run

**Platform:** SupportIQ — Customer Support Intelligence  
**Audience:** Senior Management / Non-Technical Executives  
**Duration:** 15–20 minutes  
**Base URL:** `http://localhost:8080` | Swagger: `http://localhost:8080/swagger-ui.html`

---

## Before You Begin — Pre-Demo Checklist

| Check | Command | Expected |
|---|---|---|
| App is running | `curl http://localhost:8080/api/support/tickets` | HTTP 200 |
| Demo data seeded | Response should have ≥ 20 tickets | Count ≥ 20 |
| Postman loaded | Import `postman/SupportIQ-Demo.postman_collection.json` | 4 folders visible |
| Browser ready | Open `http://localhost:8080/swagger-ui.html` | Swagger page loads |

> **If the app is not running:** `mvn spring-boot:run` — wait ~10 seconds for startup.  
> **If ticket count is 0:** Stop and restart the app. The data seeds automatically on first boot.

---

## The Business Problem (Your Opening — Say This First)

> *"Today, when a customer sends an angry email threatening to cancel their $200K enterprise contract, one of three things happens: it gets buried in the queue, a junior agent handles it without knowing the risk, or a manager finds it three hours later during an escalation meeting. We built SupportIQ to make sure none of those happen."*

**Set the scene with numbers:**
- Average support team processes **150–300 tickets/day**
- Manual triage takes **8–12 minutes per ticket**
- First-response time averages **4–6 hours** for most companies
- **34% of customers** who churn cite "slow or unhelpful support" as the reason

**What SupportIQ changes:**
- Every incoming email is analyzed in **under 2 seconds**
- Sentiment, risk, category, priority — automatically detected
- AI drafts the reply — agent reviews and sends in **90 seconds**
- High-risk tickets are automatically escalated — **zero human triage**

---

## FLOW 1 — The Raw Queue (Before AI)

**Duration:** 1 minute  
**Endpoint:** `GET /api/support/tickets`

### What to Say
> *"Let me show you what your support queue looks like right now — before AI touches it. Twenty tickets, all raw data. Can you tell me which one is the most urgent? Which customer is about to leave? Which one has a legal threat hidden in it?"*

### What to Show in Postman
Run **"02 List All Tickets"** from folder 1.

### What to Point Out in the Response
```json
[
  {
    "id": 1,
    "ticketNumber": "TKT-0001",
    "customerName": "Sarah Mitchell",
    "subject": "Double billing — urgent",
    "status": "OPEN",
    "priority": "MEDIUM",
    "sentimentScore": null,
    "escalationRequired": false
  },
  ...
]
```

- `sentimentScore: null` — no intelligence yet
- `escalationRequired: false` — AI hasn't assessed it yet
- `priority: MEDIUM` — default value, not computed
- There are 20 tickets. All look equally "medium urgent"

### Business Punchline
> *"Every morning, your support manager spends 20 minutes manually reading through these to decide what to work on first. With 5 agents and 200 tickets, someone always falls through the cracks. Let's fix that."*

---

## FLOW 2 — AI Dashboard (Queue Intelligence)

**Duration:** 2 minutes  
**Endpoint:** `GET /api/support/dashboard`

### What to Say
> *"This is the morning briefing your support manager has always needed. One call — the AI reads the entire queue and tells you exactly where you stand."*

### What to Show in Postman
Run **"01 AI Dashboard"** from folder 2.

### What to Point Out in the Response
```json
{
  "totalTickets": 20,
  "openCount": 8,
  "escalatedCount": 2,
  "criticalCount": 2,
  "averageSentimentScore": 4.8,
  "averageSentimentLabel": "SOMEWHAT_DISSATISFIED",
  "queueHealthScore": 58,
  "queueStatus": "AT_RISK",
  "topIssues": [
    "Multiple billing disputes with enterprise clients",
    "Platform outage affecting financial sector customers",
    "Account security breaches requiring GDPR response"
  ],
  "aiRecommendations": [
    "Assign senior agent to TKT-0001 and TKT-0003 immediately — enterprise churn risk",
    "Acknowledge TKT-0002 within 1 hour — GDPR 72-hour breach response clock is running",
    "Review billing process — 3 separate double-charge complaints suggest a systemic issue"
  ],
  "escalationAlert": "2 tickets require immediate manager attention — potential revenue loss > $50K",
  "categoryBreakdown": {
    "BILLING": 5,
    "TECHNICAL": 4,
    "ACCOUNT": 3,
    "COMPLAINT": 4,
    "GENERAL": 4
  }
}
```

**Walk through each field slowly:**

| Field | What it means | Why it matters |
|---|---|---|
| `queueHealthScore: 58` | AI's 0–100 score for queue health | Below 60 = manager attention needed today |
| `queueStatus: AT_RISK` | HEALTHY / AT_RISK / CRITICAL | Threshold for action |
| `escalationAlert` | Red flag if any ticket needs immediate action | Never miss a critical issue again |
| `aiRecommendations` | Specific, actionable steps | Not "check your tickets" — actual next actions |
| `categoryBreakdown` | Volume by issue type | Identify systemic product problems |
| `averageSentimentScore: 4.8` | Customer mood across the whole queue | If this drops below 3, something is systemically wrong |

### Business Punchline
> *"This replaces a 30-minute manager standup. The AI already knows what your best support manager would figure out after reading every ticket. And it's ready at 8 AM, before anyone opens their laptop."*

---

## FLOW 3 — Analyze Any Raw Email (THE HERO ENDPOINT)

**Duration:** 3 minutes — the most important demo moment  
**Endpoint:** `POST /api/support/ai/analyze`

### What to Say
> *"Now here's the most powerful feature. I'm going to paste in a raw customer email — just raw text, no ticket number, no pre-processing, nothing. Watch what the AI extracts in under 2 seconds."*

### What to Show in Postman
Run **"02 Analyze Raw Email ★ ACT 3 (HERO)"** from folder 2.

**Request body (read it aloud before sending):**
```json
{
  "rawText": "I am absolutely furious. Your system charged my corporate credit card TWICE for the annual plan — $2,400 was debited this morning on top of the charge last week. This is completely unacceptable and I am ready to dispute this with my bank and report this to the consumer protection agency. If this is not resolved TODAY I will be cancelling all 50 seats of our enterprise subscription and moving to a competitor."
}
```

> *"An angry customer. Double billing. Bank dispute threat. 50-seat enterprise cancellation. Let's see what the AI understands."*

### What to Point Out in the Response
```json
{
  "category": "BILLING",
  "subcategory": "Duplicate charge — annual plan",
  "sentimentScore": 1,
  "sentimentLabel": "VERY_ANGRY",
  "riskScore": 10,
  "escalationRequired": true,
  "escalationReason": "Customer has threatened bank dispute and cancellation of 50-seat enterprise account",
  "suggestedPriority": "CRITICAL",
  "keyIssues": [
    "Double charge of $2,400 on annual plan",
    "Threat to dispute with bank and consumer protection agency",
    "50-seat enterprise account at risk of cancellation"
  ],
  "recommendedAction": "Immediate manager callback + full refund initiated before EOD",
  "estimatedResolutionTime": "Same day — 4 hour maximum",
  "urgencyFactors": [
    "Enterprise account (50 seats) = significant ARR risk",
    "Legal threat: consumer protection agency mention",
    "Same-day ultimatum deadline",
    "Financial amount: $2,400 double charge confirmed"
  ],
  "sentimentAnalysis": "Customer is in maximum distress — language indicates immediate escalation risk to social media or legal action"
}
```

**Point these out one by one:**

| What AI detected | How it did it | Why it matters |
|---|---|---|
| `sentimentScore: 1` | Analyzed tone, punctuation, word choice | Minimum possible score — total emergency |
| `riskScore: 10` | Identified: enterprise size + legal threat + deadline | 10/10 churn risk — this is the highest possible |
| `escalationRequired: true` | Triggered by legal language + enterprise size | Automatically bypasses normal queue |
| `keyIssues` | Extracted 3 distinct problems from one paragraph | Agent doesn't have to re-read and summarize |
| `urgencyFactors` | AI explains exactly why this is critical | Justifies the escalation to management |
| `estimatedResolutionTime` | Contextual estimate based on issue type and severity | Sets internal SLA automatically |

### Run the Contrast Demo (takes 30 seconds extra — worth it)
Switch to **"02c Analyze Raw Email — Happy Customer"** and run it.

> *"Same endpoint, completely different email. Watch the numbers flip."*

```json
{
  "sentimentScore": 9,
  "sentimentLabel": "VERY_SATISFIED",
  "riskScore": 1,
  "escalationRequired": false,
  "suggestedPriority": "LOW",
  "category": "GENERAL"
}
```

> *"Score 1 to 10. Risk 1 to 10. The AI knows the difference between a customer who's about to cancel and a customer who's about to refer you. Same endpoint, same 2 seconds."*

### Business Punchline
> *"Your tier-1 agents spend 8 minutes on average triaging one email. This does it in 2 seconds, with more accuracy than a junior agent who's just had their third coffee. Every. Single. Email."*

---

## FLOW 4 — AI Drafts the Reply

**Duration:** 2 minutes  
**Endpoint:** `POST /api/support/tickets/{id}/draft-reply`

### What to Say
> *"The AI analyzed the ticket. Now it drafts the reply. Your agent doesn't write from scratch — they review, personalize, and send. Let's draft a reply for that angry billing customer."*

### What to Show in Postman
Run **"04 Draft Reply (id=1) ★ ACT 4"** from folder 2.

### What to Point Out in the Response
```json
{
  "ticketId": 1,
  "ticketNumber": "TKT-0001",
  "subject": "Re: Immediate Resolution — Your Billing Concern [TKT-0001]",
  "tone": "APOLOGETIC",
  "greeting": "Dear Sarah,",
  "body": "I want to personally apologize for the deeply frustrating experience you've had with your billing. A double charge of $2,400 is completely unacceptable, and I fully understand your anger.\n\nI have escalated this immediately to our billing team and initiated a full refund of $2,400 to your corporate card. You should see the funds reflected within 2-3 business days.\n\nI am also arranging for our enterprise account manager to call you today before 3 PM to walk through exactly what happened and ensure this never occurs again. As a valued customer with 50 seats on your plan, you deserve a direct conversation — not just a form response.\n\nRegarding your mention of external dispute options — please allow us to make this right first. We value your partnership enormously and are committed to resolving this to your complete satisfaction today.",
  "closing": "With sincere apologies,\nThe SupportIQ Customer Success Team",
  "includesRefundOffer": true,
  "keyPointsAddressed": [
    "Acknowledged the double charge explicitly",
    "Initiated refund proactively",
    "Committed to same-day manager callback",
    "Acknowledged enterprise relationship value",
    "Addressed the external dispute threat professionally"
  ],
  "suggestedFollowUpIn": "2 hours — confirm refund processing and callback completed"
}
```

**Walk through each field:**

| Field | What it shows |
|---|---|
| `tone: APOLOGETIC` | AI chose the tone based on sentiment score — not a dropdown the agent picked |
| `greeting` | Uses the customer's actual first name from the ticket |
| `body` | Addresses every `keyIssues` item from the analysis in the right order |
| `includesRefundOffer: true` | AI decided a refund offer was appropriate given the billing dispute + threat |
| `keyPointsAddressed` | Checklist for the agent to verify before sending |
| `suggestedFollowUpIn` | Sets the internal SLA for the next action |

### Show the Contrast — Happy Customer Reply
Run **"04b Draft Reply (id=13) — Happy Customer"** briefly.

```json
{
  "tone": "PROFESSIONAL",
  "includesRefundOffer": false,
  "body": "Thank you so much for your kind words about Emma..."
}
```

> *"Different customer, different tone, different content. APOLOGETIC vs PROFESSIONAL. The AI adapts to every single ticket without a human choosing the tone."*

### Business Punchline
> *"Your best agent writes a reply like this. Your newest agent writes 'Hi, sorry for the issue, we'll look into it.' With SupportIQ, every agent sends a reply that reads like your best agent wrote it. Average handle time: 8 minutes → 90 seconds."*

---

## FLOW 5 — Analyze a Stored Ticket (Persists AI Insights)

**Duration:** 1.5 minutes  
**Endpoint:** `POST /api/support/tickets/{id}/analyze`

### What to Say
> *"That was real-time analysis of raw text. Now let me show you what happens when we run AI analysis on tickets already in the system — because the AI doesn't just read, it writes its findings back into the database."*

### What to Show in Postman
Run **"03 Analyze Ticket (id=1) ★ ACT 5"** from folder 2.

### What to Point Out in the Response
```json
{
  "ticketId": 1,
  "ticketNumber": "TKT-0001",
  "category": "BILLING",
  "sentimentScore": 1,
  "sentimentLabel": "VERY_ANGRY",
  "riskScore": 10,
  "escalationRequired": true,
  "escalationReason": "Enterprise churn risk with legal threat — manager review required",
  "suggestedPriority": "CRITICAL",
  "keyIssues": ["Double charge", "50-seat cancellation threat", "Bank dispute"],
  "recommendedAction": "Immediate refund + manager callback today"
}
```

**Then run** `GET /api/support/tickets/1` and point out:
```json
{
  "status": "ESCALATED",    ← was OPEN before the analysis
  "priority": "CRITICAL",  ← was MEDIUM before the analysis
  "escalationRequired": true,
  "sentimentScore": 1,
  "sentimentLabel": "VERY_ANGRY"
}
```

> *"The ticket was OPEN with MEDIUM priority. The moment the AI analysed it, it set the status to ESCALATED and priority to CRITICAL — automatically. No human decision. No missed escalation."*

### Business Punchline
> *"This is the nightmare scenario that every support manager has — the enterprise client who's about to churn, sitting in the queue as a 'MEDIUM' ticket because nobody knew what was in it. This system knows. And it acts."*

---

## FLOW 6 — Bulk Triage (The Morning Priority Queue)

**Duration:** 2 minutes  
**Endpoint:** `POST /api/support/tickets/bulk-triage`

### What to Say
> *"Every morning, your team needs to know: of these 8 open tickets, what do we work on first? Right now that's a judgment call. Let's make it a data call."*

### What to Show in Postman
Run **"05 Bulk Triage ★ ACT 6"** from folder 2.

**Request body:**
```json
{
  "ticketIds": [1, 3, 5, 6, 7, 10, 17, 18]
}
```

### What to Point Out in the Response
```json
{
  "totalTriaged": 8,
  "escalationCount": 2,
  "triaged": [
    {
      "urgencyRank": 1,
      "ticketNumber": "TKT-0003",
      "subject": "Platform completely down — losing $15K per hour",
      "suggestedPriority": "CRITICAL",
      "urgencyReason": "Active revenue loss of $15K/hour with enterprise client — every minute of delay has direct financial impact",
      "escalationRequired": true
    },
    {
      "urgencyRank": 2,
      "ticketNumber": "TKT-0001",
      "subject": "Double billing — urgent",
      "suggestedPriority": "CRITICAL",
      "urgencyReason": "50-seat enterprise cancellation threat with legal escalation — same-day deadline",
      "escalationRequired": true
    },
    {
      "urgencyRank": 3,
      "ticketNumber": "TKT-0007",
      "subject": "API integration broken after your update",
      "suggestedPriority": "HIGH",
      "urgencyReason": "Technical outage blocking core business workflow — developer waiting",
      "escalationRequired": false
    },
    {
      "urgencyRank": 4,
      "ticketNumber": "TKT-0006",
      "subject": "Need invoice copy for audit",
      "suggestedPriority": "MEDIUM",
      "urgencyReason": "Compliance deadline — audit is scheduled for next week",
      "escalationRequired": false
    }
  ],
  "overallInsight": "Queue is in a critical state: two CRITICAL tickets with direct revenue implications must be handled before all others. Active outage (TKT-0003) should be treated as P0 — every hour of delay compounds financial and relationship damage.",
  "model": "gpt-5-mini-2025-08-07"
}
```

**Walk through the ranking:**

> *"The AI ranked 8 tickets and explained exactly why each one landed where it did. Rank 1 is the platform outage costing $15K per hour — that's not a judgment call anymore, it's a fact. Rank 2 is the billing dispute with the cancellation threat. The API issue is rank 3 — important, but nobody is losing money by the hour."*

**Point out `urgencyReason` for rank 1 and 2 specifically:**
- These are not generic labels — the AI read the ticket content and wrote a specific explanation
- An agent reading this knows *exactly* why this ranking was chosen

### Business Punchline
> *"Your 9 AM standup is: 'What's most urgent today?' This answers that question before anyone opens their email. In 2 seconds. For any size queue — 8 tickets, 800 tickets, same endpoint."*

---

## FLOW 7 — Escalation Queue (The Manager's Dashboard)

**Duration:** 1 minute  
**Endpoint:** `GET /api/support/tickets/escalation-queue`

### What to Say
> *"Final piece. Every support manager needs one view: what needs my personal attention right now? Not all open tickets — just the ones I cannot delegate."*

### What to Show in Postman
Run **"06 Escalation Queue ★ ACT 7"** from folder 2.

### What to Point Out in the Response
```json
[
  {
    "ticketNumber": "TKT-0001",
    "customerName": "Sarah Mitchell",
    "subject": "Double billing — urgent",
    "status": "ESCALATED",
    "priority": "CRITICAL",
    "sentimentScore": 1,
    "sentimentLabel": "VERY_ANGRY",
    "escalationRequired": true,
    "riskScore": 10
  },
  {
    "ticketNumber": "TKT-0002",
    "customerName": "James Thornton",
    "subject": "Possible account compromise — unauthorized access",
    "status": "ESCALATED",
    "priority": "CRITICAL",
    "sentimentScore": 2,
    "sentimentLabel": "VERY_ANGRY",
    "escalationRequired": true
  },
  {
    "ticketNumber": "TKT-0003",
    "customerName": "Rachel Kim",
    "subject": "Platform completely down — losing $15K per hour",
    "status": "CRITICAL",
    "priority": "CRITICAL",
    "escalationRequired": true
  }
]
```

> *"Three tickets. That's the manager's entire to-do list right now. Not 20 tickets. Not a Slack message saying 'check ticket #1234'. Three tickets — all of which have been confirmed as needing manager-level attention by the AI."*

### Business Punchline
> *"Nothing falls through the cracks. The AI is the first line of defence that never sleeps, never has an off day, and never misses a legal threat buried in paragraph three of an angry email."*

---

## FLOW 8 — Full Demo Scenario: End-to-End (Optional — 3 Minutes)

Run the **"4 — Demo Scenario: Full Angry Customer Flow"** folder in order if you have extra time.

This shows the complete lifecycle:
1. Raw queue → 20 tickets, no intelligence
2. Dashboard → queue health assessment  
3. Analyze TKT-0001 → triggers auto-escalation, rewrites priority to CRITICAL
4. Draft reply → APOLOGETIC tone, refund offer, all issues addressed
5. Escalation queue → TKT-0001 now appears here automatically
6. Assign agent → `IN_PROGRESS`, assigned to named agent

> *"From inbox to escalated, triaged, and replied — the AI handled every step that used to require human judgment."*

---

## Business Impact Summary

Present this slide or table at the end:

| Metric | Before SupportIQ | After SupportIQ | Improvement |
|---|---|---|---|
| Triage time per ticket | 8–12 minutes | 2 seconds | **240× faster** |
| First response time | 4–6 hours average | < 15 minutes for CRITICAL | **24× faster** |
| Escalation accuracy | Depends on agent experience | AI-consistent, always-on | **Near 100%** |
| Agent handle time | 8 minutes average | 90 seconds (review + send) | **5× faster** |
| Enterprise churn risk detection | After the customer leaves | Before they send the next email | **Proactive** |
| Manager queue review | 20–30 min standup | Real-time, always current | **Eliminated** |
| Reply quality consistency | Varies by agent | Every reply like your best agent | **Standardised** |

---

## ROI Framing (For the CFO in the Room)

> *"If your support team handles 200 tickets per day and triage takes 10 minutes each, that's 33 agent-hours per day in pure triage — work that produces no value for the customer. At fully-loaded cost of €60/hour, that's €2,000 per day in triage labour alone — €500,000 per year.*
>
> *SupportIQ eliminates triage. The same team handles more tickets, faster, with higher quality. And in the meantime, you stop losing enterprise deals to escalations that arrived too late."*

---

## Technical Architecture (One Slide — Non-Technical)

```
Customer Email
      ↓
[SupportIQ API — Spring Boot]
      ↓
[EPAM DIAL AI Proxy] ──→ [GPT-5 Language Model]
      ↓
Structured Response:
  category / sentiment / risk / escalation flag / recommended action
      ↓
[Database — ticket updated automatically]
      ↓
[Manager Dashboard] ← always current, never stale
```

**Three things to emphasise:**
1. **Your data never leaves your infrastructure** — EPAM DIAL is an internal enterprise AI proxy, not consumer ChatGPT
2. **The AI is a microservice** — it can be unplugged or swapped without touching the ticket system
3. **No training required** — the AI uses general language understanding, not fine-tuned models specific to your data

---

## Handling Tough Questions

### Q: "How accurate is the sentiment detection?"
> *"Large language models (GPT-5) score above 90% accuracy on sentiment classification tasks versus human labellers — significantly higher than rule-based keyword systems. The AI understands context: 'I'm not angry, just disappointed' scores differently from 'I am absolutely furious.' More importantly, even at 85% accuracy, it's still faster and more consistent than a stressed tier-1 agent triaging 80 tickets before lunch."*

### Q: "What if the AI gets it wrong?"
> *"The AI is an assistant, not a replacement. Every draft reply is reviewed by a human agent before it's sent. Every escalation is visible to the manager. The AI reduces cognitive load — it never makes the final call. Think of it as the world's most thorough intern who pre-reads every ticket and highlights the important parts."*

### Q: "How much does the AI cost to run?"
> *"Each API call costs fractions of a cent. Analyzing one ticket costs approximately €0.001 — less than a hundredth of what it costs in agent time. At 200 tickets per day, the AI processing cost is under €1/day. The ROI pays back in the first hour."*

### Q: "Is our customer data safe?"
> *"All AI calls go through EPAM DIAL — an enterprise AI gateway, not public-facing ChatGPT. Data is processed in transit and not stored by the model. The API key and proxy are controlled by your infrastructure team. We can also configure it to redact PII before sending to the model, if required by your data policy."*

### Q: "Can this scale to our full ticket volume?"
> *"The architecture is stateless REST API + async AI calls. Horizontal scaling is a Kubernetes config change. The bottleneck is the AI proxy throughput, not the application logic. EPAM DIAL supports enterprise-scale rate limits. For very high volumes, we add a queue between ticket creation and AI analysis — the UX is the same, the latency is just async."*

### Q: "What's the implementation timeline?"
> *"This demo runs on a Spring Boot application. Integration with an existing ticket system (Zendesk, Freshdesk, Salesforce Service Cloud) is an API adapter — typically 2–4 weeks for a production-ready connector. The AI logic is already built. You're looking at 4–6 weeks to production for a standard support platform."*

---

## Appendix — All API Endpoints Reference

### Ticket Management

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/support/tickets` | Create new ticket |
| `GET` | `/api/support/tickets` | List all tickets |
| `GET` | `/api/support/tickets?status=OPEN` | Filter by status |
| `GET` | `/api/support/tickets/{id}` | Get single ticket |
| `PATCH` | `/api/support/tickets/{id}/status` | Update status + assign agent |
| `DELETE` | `/api/support/tickets/{id}` | Delete ticket |
| `GET` | `/api/support/tickets/escalation-queue` | Escalation-flagged tickets only |

### AI Intelligence

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/support/dashboard` | AI queue health dashboard |
| `POST` | `/api/support/ai/analyze` | Analyze raw email text |
| `POST` | `/api/support/tickets/{id}/analyze` | Analyze stored ticket + persist results |
| `POST` | `/api/support/tickets/{id}/draft-reply` | AI-drafted reply for a ticket |
| `POST` | `/api/support/tickets/bulk-triage` | Rank N tickets by urgency |

### Status Values

`OPEN` → `IN_PROGRESS` → `ESCALATED` → `RESOLVED` → `CLOSED`

### Priority Values

`LOW` → `MEDIUM` → `HIGH` → `CRITICAL`

### Sentiment Scale

| Score | Label | Meaning |
|---|---|---|
| 1–2 | VERY_ANGRY | Immediate escalation risk |
| 3–4 | ANGRY | High attention needed |
| 5–6 | NEUTRAL | Standard handling |
| 7–8 | SATISFIED | Low risk |
| 9–10 | VERY_SATISFIED | Potential reference customer |

### Key Demo Tickets (Pre-Seeded)

| ID | Customer | Issue | Expected AI Output |
|---|---|---|---|
| 1 | Sarah Mitchell | Double billing $2,400 | Sentiment 1, Risk 10, CRITICAL, escalate=true |
| 2 | James Thornton | Account hack, GDPR breach | Sentiment 2, Risk 9, CRITICAL, escalate=true |
| 3 | Rachel Kim | Platform down, $15K/hr loss | Risk 10, CRITICAL, escalate=true |
| 5 | Daniel Park | Suspicious login 3rd country | Risk 8, ACCOUNT, escalate=true |
| 13 | Nina Johansson | Compliment — great onboarding | Sentiment 10, Risk 1, LOW, escalate=false |

---

*Document prepared for SupportIQ management demo — Day 8 of AI-Native Engineering Series.*  
*Platform: Spring Boot 4.1 + EPAM DIAL (GPT-5) + SupportIQ Intelligence Layer*
