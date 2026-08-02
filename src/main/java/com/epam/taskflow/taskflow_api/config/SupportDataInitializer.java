package com.epam.taskflow.taskflow_api.config;

import com.epam.taskflow.taskflow_api.model.SupportTicket;
import com.epam.taskflow.taskflow_api.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class SupportDataInitializer implements ApplicationRunner {

    private final SupportTicketRepository repository;

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            log.info("SupportIQ: demo tickets already seeded, skipping.");
            return;
        }

        List<SupportTicket> tickets = List.of(

            // --- CRITICAL escalation cases ---
            SupportTicket.builder()
                .ticketNumber("TKT-0001")
                .customerName("Sarah Mitchell")
                .customerEmail("sarah.mitchell@acmecorp.com")
                .subject("URGENT: Charged twice for annual subscription — $2,400 taken from account")
                .body("I am absolutely furious. Your system charged my corporate credit card TWICE for the annual plan — $2,400 was debited this morning on top of the charge last week. This is completely unacceptable and I am ready to dispute this with my bank and report this to the consumer protection agency. I need an IMMEDIATE full refund and an explanation. If this is not resolved TODAY I will be cancelling all 50 seats of our enterprise subscription and moving to a competitor.")
                .category("BILLING")
                .status("OPEN")
                .priority("CRITICAL")
                .sentimentScore(1)
                .sentimentLabel("VERY_ANGRY")
                .escalationRequired(true)
                .source("EMAIL")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0002")
                .customerName("James Thornton")
                .customerEmail("james.thornton@globalfinance.io")
                .subject("Account compromised — unauthorized transactions from unknown location")
                .body("My account was accessed from an IP address in Eastern Europe at 3 AM yesterday. I have 2-factor auth enabled but someone still got in. There are 3 unauthorized API calls in my logs exporting customer data. This is a potential GDPR breach. I have already notified my legal team and they are asking for an incident report within 24 hours. Please escalate this to your security team immediately.")
                .category("ACCOUNT")
                .status("ESCALATED")
                .priority("CRITICAL")
                .sentimentScore(2)
                .sentimentLabel("VERY_ANGRY")
                .escalationRequired(true)
                .source("EMAIL")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0003")
                .customerName("Rachel Kim")
                .customerEmail("r.kim@techventures.co")
                .subject("Platform completely down for 6 hours — lost $15,000 in revenue")
                .body("Your platform has been completely unresponsive for 6 hours during our biggest sales event of the year. Our entire team was unable to process orders. We estimate we lost approximately $15,000 in revenue directly attributable to this outage. I am going to post about this on LinkedIn and G2 reviews unless I get a personal call from your CTO and a written explanation with compensation offer. This is the second outage in 3 months.")
                .category("TECHNICAL")
                .status("OPEN")
                .priority("CRITICAL")
                .sentimentScore(1)
                .sentimentLabel("VERY_ANGRY")
                .escalationRequired(true)
                .source("PHONE")
                .build(),

            // --- HIGH priority angry cases ---
            SupportTicket.builder()
                .ticketNumber("TKT-0004")
                .customerName("David Chen")
                .customerEmail("dchen@startup.io")
                .subject("Refund request — product not as advertised")
                .body("I signed up for the Professional plan based on the feature list on your website which clearly states 'unlimited API calls.' After signing up I discovered that 'unlimited' actually means 10,000 calls per day. This is misleading advertising. I want a full refund for this month and I'll be downgrading immediately. Very disappointed in the deceptive marketing.")
                .category("REFUND")
                .status("OPEN")
                .priority("HIGH")
                .sentimentScore(3)
                .sentimentLabel("ANGRY")
                .escalationRequired(false)
                .source("EMAIL")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0005")
                .customerName("Maria Santos")
                .customerEmail("m.santos@retailplus.com")
                .subject("Data export broken for 2 weeks — compliance deadline at risk")
                .body("The CSV export function in the Reports section has been broken for 2 weeks. Every time I click Export it spins for 30 seconds then shows 'Export failed. Please try again.' I have a regulatory compliance audit in 10 days and I NEED this data. I have submitted 3 support tickets about this (tickets #8821, #8834, #8901) and none have been resolved. This is completely unacceptable support quality.")
                .category("TECHNICAL")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .sentimentScore(3)
                .sentimentLabel("ANGRY")
                .escalationRequired(false)
                .assignedAgent("Alex Rivera")
                .source("EMAIL")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0006")
                .customerName("Tom Becker")
                .customerEmail("tbecker@manufacturing-co.de")
                .subject("Invoice showing wrong company name — tax filing issue")
                .body("All invoices from the last 3 months show 'Becker Industries' but our legal company name changed to 'Becker GmbH' in January. I have emailed accounts@yourcompany.com 4 times with no response. I cannot file my quarterly VAT return in Germany with incorrect invoice names. Please correct all historical invoices and send corrected PDFs urgently. Our accountant is waiting.")
                .category("BILLING")
                .status("OPEN")
                .priority("HIGH")
                .sentimentScore(4)
                .sentimentLabel("ANGRY")
                .escalationRequired(false)
                .source("EMAIL")
                .build(),

            // --- MEDIUM priority neutral/frustrated cases ---
            SupportTicket.builder()
                .ticketNumber("TKT-0007")
                .customerName("Priya Patel")
                .customerEmail("priya@designstudio.in")
                .subject("Mobile app crashes on iOS 17.4 when uploading files > 10MB")
                .body("Since updating to iOS 17.4 last week, the mobile app crashes every time I try to upload a file larger than 10MB. Smaller files work fine. I have tried reinstalling the app 3 times. My device is iPhone 14 Pro with 512GB storage, so it's not a storage issue. Please fix this — I use the mobile app constantly for client presentations.")
                .category("TECHNICAL")
                .status("IN_PROGRESS")
                .priority("MEDIUM")
                .sentimentScore(5)
                .sentimentLabel("NEUTRAL")
                .escalationRequired(false)
                .assignedAgent("Sophie Laurent")
                .source("MOBILE")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0008")
                .customerName("Kevin Walsh")
                .customerEmail("kwalsh@consulting.net")
                .subject("API rate limit errors even though I'm on Enterprise tier")
                .body("I'm on the Enterprise tier which is supposed to have no rate limits, but I'm getting 429 Too Many Requests errors when my integration sends more than 500 requests per minute. My contract clearly states 'unlimited API calls' for Enterprise. I've checked my API key settings and everything looks correct. Can you please check if there's a misconfiguration on my account? Account ID: ENT-4892.")
                .category("TECHNICAL")
                .status("OPEN")
                .priority("MEDIUM")
                .sentimentScore(5)
                .sentimentLabel("NEUTRAL")
                .escalationRequired(false)
                .source("API")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0009")
                .customerName("Amelia Foster")
                .customerEmail("afoster@healthtech.org")
                .subject("Request for HIPAA Business Associate Agreement")
                .body("We are a healthcare technology company and need a signed Business Associate Agreement (BAA) before we can proceed with storing any patient-related data on your platform. Our legal team requires this before we can expand our usage. Could you please send the BAA for review? We are currently on the Professional plan and considering upgrading to Enterprise specifically for HIPAA compliance features.")
                .category("ACCOUNT")
                .status("OPEN")
                .priority("MEDIUM")
                .sentimentScore(6)
                .sentimentLabel("NEUTRAL")
                .escalationRequired(false)
                .source("EMAIL")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0010")
                .customerName("Lucas Fernandez")
                .customerEmail("l.fernandez@ecommerce-mx.com")
                .subject("Webhook notifications stopped working after last deployment")
                .body("Our webhook integrations stopped sending notifications about 48 hours ago. I checked the webhook logs in the dashboard and they show 'Delivery Failed' for all events. The endpoint URL is correct and accessible — I tested it manually. This is affecting our order processing automation. Webhook URL: https://api.ecommerce-mx.com/webhooks/platform. Last successful delivery was 2 days ago.")
                .category("TECHNICAL")
                .status("IN_PROGRESS")
                .priority("MEDIUM")
                .sentimentScore(4)
                .sentimentLabel("ANGRY")
                .escalationRequired(false)
                .assignedAgent("Marcus Chen")
                .source("EMAIL")
                .build(),

            // --- Resolved / Closed cases (show history) ---
            SupportTicket.builder()
                .ticketNumber("TKT-0011")
                .customerName("Claire Dubois")
                .customerEmail("claire.dubois@frenchbakery.fr")
                .subject("How to export data to Excel format?")
                .body("Hello, I would like to export my monthly report data to Excel format but I can only find the CSV option. Is it possible to export directly as .xlsx? If not, is there a workaround? Thank you.")
                .category("GENERAL")
                .status("RESOLVED")
                .priority("LOW")
                .sentimentScore(7)
                .sentimentLabel("SATISFIED")
                .escalationRequired(false)
                .assignedAgent("Sophie Laurent")
                .source("CHAT")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0012")
                .customerName("Ahmed Al-Rashid")
                .customerEmail("ahmed@dubai-consultants.ae")
                .subject("Password reset email not arriving")
                .body("I requested a password reset 30 minutes ago but haven't received the email. I've checked spam folders. My email is ahmed@dubai-consultants.ae. Could you please resend it or reset it manually? I have an important meeting in 1 hour and need to access my account.")
                .category("ACCOUNT")
                .status("RESOLVED")
                .priority("MEDIUM")
                .sentimentScore(6)
                .sentimentLabel("NEUTRAL")
                .escalationRequired(false)
                .assignedAgent("Alex Rivera")
                .source("CHAT")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0013")
                .customerName("Nina Johansson")
                .customerEmail("nina.j@nordic-retail.se")
                .subject("Compliment — your onboarding team was exceptional")
                .body("I just wanted to take a moment to say how impressed I am with your onboarding team. Emma from customer success spent 2 hours with me setting up integrations and was incredibly knowledgeable and patient. The platform itself is also fantastic — our team productivity has increased noticeably in just the first week. Please pass on my thanks to Emma and her manager. Will definitely be recommending your platform to others in our network.")
                .category("GENERAL")
                .status("CLOSED")
                .priority("LOW")
                .sentimentScore(10)
                .sentimentLabel("VERY_SATISFIED")
                .escalationRequired(false)
                .source("EMAIL")
                .build(),

            // --- LOW priority informational ---
            SupportTicket.builder()
                .ticketNumber("TKT-0014")
                .customerName("Brandon Taylor")
                .customerEmail("btaylor@nonprofit.org")
                .subject("Nonprofit discount — do you offer one?")
                .body("We are a registered 501(c)(3) nonprofit organization focused on youth education. I noticed several SaaS companies offer nonprofit discounts of 30-50%. Do you have a nonprofit pricing program? We currently have 15 users and would love to expand to 40+ users if the pricing works for our budget. Our EIN is 82-1234567.")
                .category("BILLING")
                .status("OPEN")
                .priority("LOW")
                .sentimentScore(7)
                .sentimentLabel("SATISFIED")
                .escalationRequired(false)
                .source("EMAIL")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0015")
                .customerName("Yuki Tanaka")
                .customerEmail("yuki.tanaka@techstartup.jp")
                .subject("SSO integration with Okta — documentation needed")
                .body("We are setting up SSO integration using Okta and I have a few technical questions about the SAML 2.0 configuration. Specifically: 1) What is the correct ACS URL for the SAML assertion consumer? 2) Does your platform support just-in-time provisioning? 3) Is there a way to map Okta groups to your platform roles? Our IT team would appreciate any documentation or a technical walkthrough call.")
                .category("TECHNICAL")
                .status("OPEN")
                .priority("LOW")
                .sentimentScore(7)
                .sentimentLabel("SATISFIED")
                .escalationRequired(false)
                .source("EMAIL")
                .build(),

            // --- More variety for bulk triage demo ---
            SupportTicket.builder()
                .ticketNumber("TKT-0016")
                .customerName("Robert Chambers")
                .customerEmail("rchambers@legalfirm.com")
                .subject("Data retention policy — need 7-year data archive for legal compliance")
                .body("As a law firm, we are required by bar association rules to retain client matter data for a minimum of 7 years. Your standard plan only offers 2-year retention. We need to understand if you offer extended data retention options and at what cost. Our compliance officer will need written documentation of your data retention and deletion policies. This affects our ability to remain on your platform.")
                .category("ACCOUNT")
                .status("OPEN")
                .priority("MEDIUM")
                .sentimentScore(6)
                .sentimentLabel("NEUTRAL")
                .escalationRequired(false)
                .source("EMAIL")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0017")
                .customerName("Isabella Romano")
                .customerEmail("i.romano@fashionbrand.it")
                .subject("Dashboard loading extremely slowly — 15+ seconds")
                .body("For the past week, the main dashboard takes 15-20 seconds to load. It was instant before. I have tested on Chrome, Firefox, and Safari with the same result. My internet connection is fine (200Mbps fiber). Other websites load instantly. The data shown on the dashboard is not even that large — we only have 200 tasks. Is there a performance issue on your end?")
                .category("TECHNICAL")
                .status("OPEN")
                .priority("MEDIUM")
                .sentimentScore(4)
                .sentimentLabel("ANGRY")
                .escalationRequired(false)
                .source("CHAT")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0018")
                .customerName("Derek Washington")
                .customerEmail("derek@mediagroupUSA.com")
                .subject("Bulk user import failing — 500 Internal Server Error")
                .body("Trying to bulk import 250 users from a CSV file using your import tool. Every time I upload the file it processes for about 2 minutes then returns a 500 Internal Server Error. I have tried with smaller batches (50 users) and those work fine. The file format is exactly as shown in your documentation. We're trying to onboard our new marketing department and this is blocking us completely.")
                .category("TECHNICAL")
                .status("IN_PROGRESS")
                .priority("HIGH")
                .sentimentScore(4)
                .sentimentLabel("ANGRY")
                .escalationRequired(false)
                .assignedAgent("Marcus Chen")
                .source("EMAIL")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0019")
                .customerName("Sophia Chang")
                .customerEmail("sophia.chang@fintech-asia.sg")
                .subject("Two-factor authentication not working — locked out of account")
                .body("I recently changed phones and my Google Authenticator app no longer has my account. I have the backup codes I saved when I set up 2FA, but when I enter them it says 'Invalid backup code.' I've tried all 10 codes. I cannot access my account at all. This is urgent as I have an investor presentation in 2 hours that requires data from the platform. Please help!")
                .category("ACCOUNT")
                .status("OPEN")
                .priority("HIGH")
                .sentimentScore(3)
                .sentimentLabel("ANGRY")
                .escalationRequired(false)
                .source("CHAT")
                .build(),

            SupportTicket.builder()
                .ticketNumber("TKT-0020")
                .customerName("Mark Petrov")
                .customerEmail("m.petrov@saas-agency.ru")
                .subject("Feature request: Slack integration for ticket notifications")
                .body("We would love to see a native Slack integration that sends notifications when tickets are updated or comments are added. Currently we have to manually check the platform. I know you have a Zapier integration but we'd prefer native support. Is this on your roadmap? We'd be willing to participate in a beta program if you're developing this feature. This would be a 10/10 improvement for our team's workflow.")
                .category("GENERAL")
                .status("CLOSED")
                .priority("LOW")
                .sentimentScore(8)
                .sentimentLabel("SATISFIED")
                .escalationRequired(false)
                .source("EMAIL")
                .build()
        );

        repository.saveAll(tickets);
        log.info("SupportIQ: seeded {} demo support tickets.", tickets.size());
    }
}
