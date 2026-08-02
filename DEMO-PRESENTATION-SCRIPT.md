# SupportIQ — Live Demo Presentation Script
### Read this word-for-word while sharing your screen

---

> **HOW TO USE THIS SCRIPT**
> - `SAY:` → Read these words out loud, exactly as written
> - `[ACTION]` → Do this on your screen before speaking the next line
> - `[POINT TO]` → Move your mouse cursor to this area so audience eyes follow
> - `[PAUSE]` → Stop talking for 2–3 seconds. Let them look. Silence is powerful.
> - Everything in `( )` is a note for you — do NOT read it out loud

---

## BEFORE YOU START — Setup Checklist

> ( Do all of this BEFORE you share your screen )

- [ ] VPN connected to EPAM network
- [ ] `mvn spring-boot:run` started and app is running
- [ ] Browser open at `http://localhost:8080`
- [ ] Browser zoom at 100% — not zoomed in or out
- [ ] Postman open with SupportIQ collection loaded — minimised in taskbar
- [ ] This script open on your phone or a second monitor
- [ ] Glass of water nearby

---

## OPENING — Before Touching the Computer
### ( Stand up straight. Speak slowly. Make eye contact before you start. )

---

SAY:
> "Good morning everyone. Thank you for making time for this.
>
> I want to start with a question — and I want you to think about this honestly.
>
> Right now, today, if an enterprise client sends your support team an email threatening to cancel their contract... how long does it take for a manager to know about it?"

[PAUSE — 4 seconds. Let them think.]

SAY:
> "In most companies — the honest answer is: it depends on which agent opens that email, whether they recognise the risk, and whether they have time to escalate it. Sometimes it's 20 minutes. Sometimes it's 4 hours. Sometimes the manager finds out when the client has already left.
>
> What I'm going to show you today is a system where the answer is always: 2 seconds."

[PAUSE — 3 seconds.]

SAY:
> "This is SupportIQ — an AI-powered customer support intelligence platform I've built on top of our existing task management API. It connects directly to EPAM's enterprise AI infrastructure.
>
> I'm going to walk you through seven things the system can do — live, right now, with real data. Let's start."

---

## ACT 1 — The Raw Queue ( ~1 minute )
### ( Share your screen now. Browser should be open at http://localhost:8080 )

---

[ACTION: Make sure you are on the Dashboard page. The browser shows http://localhost:8080]

SAY:
> "This is the SupportIQ platform. What you're looking at right now is the AI dashboard — but before I show you the intelligence, I want to show you the problem it's solving."

[ACTION: Click **"All Tickets"** in the left sidebar]

[PAUSE — wait for the ticket table to fully load]

SAY:
> "This is your support queue. Twenty tickets from real customer scenarios — billing disputes, security breaches, platform outages, technical issues, and happy customers mixed in."

[POINT TO: the ticket table — sweep your cursor slowly down the list]

SAY:
> "Look at this. Every ticket has a status — OPEN. Every ticket has a priority — MEDIUM. The sentiment column is empty. The risk column is empty.
>
> This is what your support team sees every morning when they arrive. Twenty items. All looking equally urgent. All requiring human judgment to prioritise."

[PAUSE — 3 seconds.]

SAY:
> "Now — I want you to find Sarah Mitchell's ticket. She's in row one. Her subject is 'Double billing — urgent'. She has 50 enterprise seats. She is threatening to dispute with her bank and cancel her entire contract — today.
>
> But looking at this table right now — does she look different from the person asking for an invoice copy in row six?"

[PAUSE — 3 seconds. Let them look.]

SAY:
> "That is the problem. Every ticket looks the same until someone reads it. Let me show you what AI does to this picture."

---

## ACT 2 — AI Dashboard ( ~2 minutes )
### ( This is your first WOW moment )

---

[ACTION: Click **"Dashboard"** in the left sidebar]

[PAUSE — wait for the dashboard to fully load. The health ring and numbers should appear.]

SAY:
> "This is the AI intelligence dashboard. The moment I open this — the system has already read every ticket in the queue, analysed the language, and given me a complete picture of where we stand."

[POINT TO: the big circular health score ring on the left]

SAY:
> "This number here — the queue health score — is the AI's overall assessment of how well the support operation is performing right now, on a scale of zero to one hundred."

[PAUSE — 2 seconds.]

SAY:
> "Below the score you can see the status — HEALTHY means we're on top of things, AT RISK means we need attention today, CRITICAL means something needs to happen in the next hour."

[POINT TO: the four stat cards at the top — Total, Open, Escalated, Critical]

SAY:
> "These four numbers tell me the shape of the queue. Total tickets, how many are open, how many have been flagged for escalation, and how many are at the highest priority level."

[POINT TO: the AI Recommendations section at the bottom]

SAY:
> "Now — this is the part I want you to focus on. These recommendations. Not generic advice like 'respond to your customers faster'. Read what it actually says."

[PAUSE — 5 seconds. Let them read the recommendations on screen.]

SAY:
> "The AI read the ticket content and told me specifically — which ticket needs senior agent attention, which one has a GDPR clock running, and that three separate billing complaints suggest a systemic issue in the product.
>
> That insight — used to take a support manager thirty minutes of reading every morning. Now it is ready before anyone opens their laptop."

[PAUSE — 3 seconds.]

SAY:
> "But the dashboard is just the summary. Let me show you the most powerful feature in this system."

---

## ACT 3 — Analyze Any Raw Email ★ THE HERO MOMENT ( ~3 minutes )
### ( This is the centrepiece of the demo. Slow down. Let every number land. )

---

[ACTION: Click **"Analyze Email"** in the left sidebar]

SAY:
> "This is what I call the hero endpoint. Watch what I'm about to do carefully.
>
> I am going to paste a raw customer email — just plain text, copied from an inbox. No ticket number. No classification. No routing. Just words a customer typed when they were angry."

[ACTION: Click the button **"Load: Angry Billing"** to pre-fill the text area]

SAY:
> "This is the email. Read it on screen."

[PAUSE — 8 full seconds. Let everyone read the email. Do NOT speak during this pause.]

SAY:
> "This is a person who has been charged twice. They're threatening their bank, threatening the consumer protection agency, and they have fifty enterprise seats they're ready to cancel — today.
>
> Your tier-one agent receives this email at 9 AM on a Monday morning along with forty-seven other emails. Let's see what happens in two seconds."

[ACTION: Click the **"⚡ Analyze with AI"** button]

[PAUSE — wait for the results to appear on the right side of the screen. Do not speak while it loads.]

[POINT TO: the Sentiment Score metric box]

SAY:
> "Sentiment score: one out of ten."

[PAUSE — 2 seconds.]

SAY:
> "One. The lowest possible score. The AI read the language — 'absolutely furious', 'completely unacceptable', 'I am ready' — and it understood the emotional state of this customer. Not from a keyword list. From understanding the meaning."

[POINT TO: the Risk Score metric box]

SAY:
> "Risk score: ten out of ten."

[PAUSE — 2 seconds.]

SAY:
> "The system looked for churn risk factors. It found: an enterprise account with fifty seats, a legal threat with the consumer protection agency, a same-day ultimatum, and a specific financial amount — $2,400 double charged. That combination means maximum risk."

[POINT TO: the ESCALATION REQUIRED badge]

SAY:
> "And here — escalation required: true. The system has decided, automatically, that this ticket cannot go to a junior agent. It needs a manager."

[POINT TO: the Key Issues section]

SAY:
> "These are the key issues the AI extracted from that paragraph of text. 'Double charge of $2,400', 'threat to dispute with bank', '50-seat enterprise account at risk'. Your agent no longer has to read and summarise. The AI already did it."

[POINT TO: the Recommended Action section]

SAY:
> "And then it tells your agent exactly what to do: 'Immediate manager callback plus full refund initiated before end of day.' Estimated resolution time: same day."

[PAUSE — 4 seconds. Let everything sink in.]

SAY:
> "From email to full intelligence report — two seconds. Every single email. Twenty-four hours a day. Without getting tired, without having a bad day, and without missing the legal threat buried in paragraph three."

[PAUSE — 3 seconds.]

SAY:
> "Now let me show you something quickly — the same system on a completely different email."

[ACTION: Click **"Load: Happy Customer"** button]

[ACTION: Click **"⚡ Analyze with AI"** button]

[PAUSE — wait for results]

SAY:
> "Sentiment score nine out of ten. Risk score one out of ten. No escalation required. Category: general.
>
> The system understood the difference between a customer who is about to leave and a customer who is about to refer you. Same endpoint. Same two seconds. Same intelligence."

---

## ACT 4 — AI Drafts the Reply ( ~2 minutes )

---

[ACTION: Click **"All Tickets"** in the left sidebar]

SAY:
> "Now — the AI has analysed the email. The next thing your agent needs to do is respond. And this is where most companies lose time. Writing a professional reply to an angry customer is hard. It takes experience. It takes the right tone. It takes knowing what to promise and what not to promise.
>
> Let me show you how the AI handles this."

[ACTION: Find TKT-0001 in the ticket table — Sarah Mitchell's row. Click the **"Reply"** button on that row.]

[PAUSE — wait for the modal to open with the drafted reply]

SAY:
> "The AI has drafted a complete, ready-to-send reply. Look at the top."

[POINT TO: the APOLOGETIC tone badge]

SAY:
> "Tone: APOLOGETIC. The system chose this automatically based on the sentiment score and the category. It did not ask the agent to select a tone from a dropdown. It decided."

[POINT TO: the full reply text in the modal]

SAY:
> "Read this reply. This is not a template. This is not 'Dear Customer, we are sorry for the inconvenience.' This is a personalised response that — and I want you to notice this — addresses the double charge specifically, commits to a same-day refund, offers a manager callback, acknowledges the 50-seat relationship, and professionally responds to the legal threat — all without escalating the confrontation."

[PAUSE — 5 seconds. Let them read.]

SAY:
> "And look at the bottom — 'Key Points Addressed'. A checklist. The agent reviews this list before sending to make sure nothing was missed."

[PAUSE — 2 seconds.]

SAY:
> "Your best agent writes a reply like this. Your newest agent, fresh out of training, writes 'Hi, sorry for the issue, we will look into it.'
>
> With SupportIQ, every reply sounds like your best agent wrote it. Average handle time drops from eight minutes to ninety seconds — because the agent is reviewing and sending, not writing from scratch."

[ACTION: Close the modal by clicking the X]

---

## ACT 5 — Analyze a Stored Ticket — Auto Escalation ( ~1.5 minutes )

---

SAY:
> "Now let me show you something different. The raw email analysis works on any text you paste. But for tickets that are already in the system — the AI doesn't just analyse them, it updates them."

[ACTION: Find TKT-0001 in the ticket table. Click the **"Analyze"** button on that row.]

[PAUSE — wait for the modal to show analysis results]

SAY:
> "The AI has analysed this stored ticket. Same results as before — sentiment one, risk ten, critical priority. But now — watch what happened to the ticket itself."

[ACTION: Close the modal]

[POINT TO: Sarah Mitchell's row in the ticket table — specifically the Status and Priority columns]

SAY:
> "The status was OPEN when we started. The priority was MEDIUM. Look at it now."

[PAUSE — 3 seconds. Let them see the ESCALATED status and CRITICAL priority.]

SAY:
> "Status: ESCALATED. Priority: CRITICAL. The AI wrote these values back into the database — automatically. No human made that decision. No one had to remember to escalate it.
>
> The moment the AI touched this ticket, it promoted it to the manager's emergency lane."

[PAUSE — 3 seconds.]

SAY:
> "This is the nightmare scenario every support manager has — the enterprise client about to churn, sitting in the queue labelled as MEDIUM priority because a tired junior agent didn't catch the risk. That can never happen with this system."

---

## ACT 6 — Bulk Triage — The Morning Queue ( ~2 minutes )

---

[ACTION: Click **"Bulk Triage"** in the left sidebar]

SAY:
> "Every morning, your support team needs to answer one question: of all the open tickets we have, which ones do we work on first? Right now — that answer comes from gut instinct, seniority, and whoever shouts loudest.
>
> Let me show you how AI answers that question."

[ACTION: Click several checkboxes to select TKT-0001, TKT-0003, TKT-0005, TKT-0006, TKT-0007, TKT-0010, TKT-0017, TKT-0018 — or any 6-8 tickets visible in the checkbox list]

SAY:
> "I've selected eight open tickets. Mix of priorities, categories, customer types. Now let me ask the AI to rank them."

[ACTION: Click the **"⚡ Run Bulk Triage"** button]

[PAUSE — wait for the ranked results to appear on the right side]

SAY:
> "The AI has ranked eight tickets by urgency. Number one, most critical — look at the reason."

[POINT TO: the #1 ranked ticket and its urgencyReason text]

SAY:
> "Read that reason. It's not 'HIGH priority' or 'CRITICAL status'. It's a specific explanation — written by the AI — of exactly why this ticket is ranked first. Active revenue loss. Enterprise account. Every minute of delay has a direct financial impact.
>
> Your team lead reads that and knows exactly why they are picking up this ticket before the others."

[POINT TO: the #2 and #3 ranked items]

SAY:
> "Number two — another enterprise risk with a same-day deadline. Number three — a technical outage blocking a workflow.
>
> And look at number four and five — they are important, but they are not emergencies. The AI made that distinction so your agents don't have to argue about it in the morning standup."

[POINT TO: the Overall Insight section]

SAY:
> "And at the top — the overall insight. One paragraph that gives a support manager the full picture of what this group of tickets means and what needs to happen first.
>
> Your 9 AM standup question — 'what are we working on today?' — is now answered before anyone opens their laptop."

---

## ACT 7 — Escalation Queue — The Manager's View ( ~1 minute )

---

[ACTION: Click **"Escalation Queue"** in the left sidebar]

[PAUSE — wait for the escalation queue to load]

SAY:
> "Final piece. This is the manager's view. Not all twenty tickets. Not all open tickets. Only the tickets where the AI has determined that a manager needs to be personally involved."

[POINT TO: the red alert banner at the top showing how many tickets need attention]

SAY:
> "Right now — this is your emergency lane. Everything here has been flagged by the AI as beyond what a junior agent can handle. Legal threats. Enterprise churn risk. Security breaches with GDPR implications.
>
> Nothing falls through the cracks. The AI is the first line of defence that never sleeps, never has an off day, and never misses the legal threat buried in paragraph three of a customer email."

[PAUSE — 4 seconds.]

SAY:
> "Each card here has a Draft Reply button. One click — the AI writes the reply. The manager reviews, personalises if needed, and sends. Manager response time to a critical ticket: under five minutes."

---

## CLOSING — The Business Case ( ~1.5 minutes )
### ( Close the laptop lid or step back from the screen. Speak directly to the room. )

---

[ACTION: Do not click anything. Just speak to the room now.]

SAY:
> "Let me put this in numbers for a moment.
>
> If your support team handles two hundred tickets per day, and triage — the act of reading and deciding what to do — takes ten minutes per ticket... that is thirty-three agent hours per day in work that produces zero value for the customer. Just reading and sorting.
>
> At a fully loaded cost of sixty euros per hour — that is two thousand euros per day in triage labour alone. Five hundred thousand euros a year. Just to sort the queue."

[PAUSE — 3 seconds.]

SAY:
> "SupportIQ eliminates triage. The same team handles more tickets, with higher quality, and zero missed escalations.
>
> And we have not talked about the cost of a missed escalation. One enterprise client who cancels because their ticket sat as MEDIUM priority for four hours — that single event costs more than the entire AI infrastructure for a year."

[PAUSE — 4 seconds.]

SAY:
> "Everything I showed you today is running live, right now, on a Spring Boot API connected to EPAM's enterprise AI proxy. Your data does not leave our infrastructure. This is not a prototype. This is a production-ready architecture.
>
> The question is not whether we can build this. I just showed you that we already have.
>
> The question is: how quickly do we want to put it in front of real customers?"

[PAUSE — 5 seconds. Do not break the silence. Let them respond first.]

---

## QUESTION & ANSWER — Ready Responses

> ( Read the question the audience asks, find it below, and read your answer calmly. )

---

### Q: "How accurate is the AI? What if it makes a mistake?"

SAY:
> "Great question — and an important one. The AI is an assistant, not a replacement. Every draft reply is reviewed by a human agent before it is sent. Every escalation decision is visible to the manager who can override it.
>
> What the AI does is eliminate the mistakes of volume — the ticket that slips through because an agent had sixty others to read that morning. The AI reads every single one with the same level of attention. Even at 90% accuracy, it is more consistent than a stressed junior agent before their second coffee.
>
> And when it is wrong, we learn from that to improve the prompts. The system gets smarter over time."

---

### Q: "Is our customer data safe? Is it going to OpenAI?"

SAY:
> "No — and this was a deliberate architectural decision. All AI calls go through EPAM DIAL — our own internal enterprise AI gateway, not public ChatGPT. The data is processed in transit and is not stored or used for model training.
>
> The API key is controlled by our infrastructure. If needed, we can also add PII redaction before any text leaves our system — so personally identifiable information like names and email addresses are removed before the AI sees them."

---

### Q: "How much does it cost to run?"

SAY:
> "Each AI call costs fractions of a cent. Analysing one ticket costs approximately one tenth of a euro cent. At two hundred tickets per day, the AI processing cost is under one euro per day — less than the coffee your support manager drinks while manually sorting the queue.
>
> The return on investment is not measured in months. It is measured in the first week."

---

### Q: "What is the implementation timeline?"

SAY:
> "The core AI logic you just saw — it is already built. Integration with your existing ticket system, whether that is Zendesk, Freshdesk, or Salesforce Service Cloud, is an API adapter. Typically two to four weeks for a production-ready connector.
>
> You are looking at six to eight weeks to go from what I showed you today to agents using this in their daily workflow."

---

### Q: "What happens if DIAL goes down? Does our support stop working?"

SAY:
> "The AI is a layer on top of the existing system — not a replacement for it. If DIAL is unavailable, tickets are still created, still routed, and agents still work them manually — exactly as they do today. The AI features gracefully degrade, they do not break the core workflow.
>
> Think of it as power windows in a car. If they stop working, you can still open the window manually. The car still drives."

---

### Q: "Can this scale if we have 2,000 tickets per day instead of 200?"

SAY:
> "Yes — the architecture is stateless REST plus async AI calls. Horizontal scaling is a Kubernetes configuration change, not a code change. The bottleneck, if any, is the DIAL proxy throughput, which supports enterprise-scale rate limits.
>
> For very high volumes, we add a message queue between ticket creation and AI analysis. The user experience stays identical — the latency simply becomes asynchronous."

---

### Q: "Who maintains this after it is built?"

SAY:
> "The codebase follows standard Spring Boot conventions — any Java engineer can maintain it. The AI behaviour is controlled by system prompts in the service layer, which are readable, editable strings. Tuning the AI does not require a data scientist — it requires understanding what you want the output to look like and adjusting the instruction accordingly.
>
> We also have a full test suite — one hundred and three tests — that run on every commit, so regressions are caught before they reach production."

---

## BACKUP — If Something Goes Wrong

> ( Stay calm. Read this. )

---

**If the app is not responding:**

SAY:
> "The live demo requires a connection to EPAM's AI infrastructure — let me switch to showing you the Postman collection where I have pre-run examples saved, and we can walk through the actual API responses."

( Switch to Postman and run the pre-saved requests. The results will be the same. )

---

**If an AI call takes too long:**

SAY:
> "The AI is calling EPAM DIAL — the response time varies depending on current load. In production this would typically be under two seconds. While it processes, let me explain what the system is doing under the hood..."

( Talk about the architecture — DIAL gateway, JSON parsing, response structure — until the call completes. )

---

**If you forget what to say next:**

SAY:
> "Let me just pause here for a second — I want to make sure I am explaining this clearly. What you are looking at is..."

( Scroll up in this script on your phone to find where you are. )

---

## FINAL NOTES

- **Speak slowly.** You will feel like you are going too slow. You are not.
- **Pause after every important number.** Let 1-out-of-10 land before you explain it.
- **Do not apologise** if something loads slowly. Say "while this loads" and keep talking.
- **The silence after your closing is intentional.** Do not fill it. Wait for them to speak first.
- **You built this.** Speak with confidence. You know more about this system than anyone in that room.

---

*Script prepared for SupportIQ management demo — Day 8 of AI-Native Engineering Series*
