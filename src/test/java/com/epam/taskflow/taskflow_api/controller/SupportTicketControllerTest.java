package com.epam.taskflow.taskflow_api.controller;

import com.epam.taskflow.taskflow_api.dto.TicketCreateRequestDTO;
import com.epam.taskflow.taskflow_api.dto.TicketResponseDTO;
import com.epam.taskflow.taskflow_api.dto.TicketStatusUpdateRequestDTO;
import com.epam.taskflow.taskflow_api.exception.GlobalExceptionHandler;
import com.epam.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.epam.taskflow.taskflow_api.service.SupportTicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupportTicketController Validation Tests")
class SupportTicketControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SupportTicketService ticketService;

    @InjectMocks
    private SupportTicketController controller;

    private TicketCreateRequestDTO validRequest;
    private TicketResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();

        validRequest = TicketCreateRequestDTO.builder()
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .subject("Test subject")
                .body("Test body content")
                .build();

        sampleResponse = TicketResponseDTO.builder()
                .id(1L)
                .ticketNumber("TKT-0001")
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .subject("Test subject")
                .body("Test body content")
                .category("GENERAL")
                .status("OPEN")
                .priority("MEDIUM")
                .build();
    }

    // ─── Create Ticket ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create ticket and return 201 when all fields are valid")
    void createTicket_shouldReturn201_whenValidRequest() throws Exception {
        when(ticketService.createTicket(any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketNumber").value("TKT-0001"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DisplayName("Should reject blank customer name - returns 400")
    void createTicket_shouldReturn400_whenCustomerNameIsBlank() throws Exception {
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .customerName("   ")
                .customerEmail("john@example.com")
                .subject("Test subject")
                .body("Test body")
                .build();

        mockMvc.perform(post("/api/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Customer name is required")));
    }

    @Test
    @DisplayName("Should reject null customer email - returns 400")
    void createTicket_shouldReturn400_whenEmailIsNull() throws Exception {
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .customerName("John Doe")
                .customerEmail(null)
                .subject("Test subject")
                .body("Test body")
                .build();

        mockMvc.perform(post("/api/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Customer email is required")));
    }

    @Test
    @DisplayName("Should reject invalid email format - returns 400")
    void createTicket_shouldReturn400_whenEmailIsInvalid() throws Exception {
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .customerName("John Doe")
                .customerEmail("not-an-email")
                .subject("Test subject")
                .body("Test body")
                .build();

        mockMvc.perform(post("/api/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("valid email")));
    }

    @Test
    @DisplayName("Should reject blank subject - returns 400")
    void createTicket_shouldReturn400_whenSubjectIsBlank() throws Exception {
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .subject("")
                .body("Test body")
                .build();

        mockMvc.perform(post("/api/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Subject is required")));
    }

    @Test
    @DisplayName("Should reject blank body - returns 400")
    void createTicket_shouldReturn400_whenBodyIsBlank() throws Exception {
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .subject("Test subject")
                .body("")
                .build();

        mockMvc.perform(post("/api/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Body is required")));
    }

    @Test
    @DisplayName("Should reject subject exceeding 255 characters - returns 400")
    void createTicket_shouldReturn400_whenSubjectExceeds255Chars() throws Exception {
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .subject("a".repeat(256))
                .body("Test body")
                .build();

        mockMvc.perform(post("/api/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Subject cannot exceed 255 characters")));
    }

    @Test
    @DisplayName("Should reject body exceeding 5000 characters - returns 400")
    void createTicket_shouldReturn400_whenBodyExceeds5000Chars() throws Exception {
        TicketCreateRequestDTO request = TicketCreateRequestDTO.builder()
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .subject("Test subject")
                .body("a".repeat(5001))
                .build();

        mockMvc.perform(post("/api/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Body cannot exceed 5000 characters")));
    }

    // ─── Get Tickets ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all tickets - 200 OK")
    void getTickets_shouldReturn200_withAllTickets() throws Exception {
        when(ticketService.getAllTickets()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/support/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ticketNumber").value("TKT-0001"));
    }

    @Test
    @DisplayName("Should return filtered tickets by status - 200 OK")
    void getTickets_shouldReturn200_withFilteredByStatus() throws Exception {
        when(ticketService.getTicketsByStatus("OPEN")).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/support/tickets").param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Should return 404 when ticket not found by ID")
    void getTicketById_shouldReturn404_whenNotFound() throws Exception {
        when(ticketService.getTicketById(99L))
                .thenThrow(new ResourceNotFoundException("Support ticket not found with id: 99"));

        mockMvc.perform(get("/api/support/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    // ─── Update Status ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should update status - 200 OK")
    void updateStatus_shouldReturn200_whenValid() throws Exception {
        TicketStatusUpdateRequestDTO request = TicketStatusUpdateRequestDTO.builder()
                .status("IN_PROGRESS")
                .assignedAgent("Sarah")
                .build();

        when(ticketService.updateStatus(eq(1L), any())).thenReturn(
                TicketResponseDTO.builder()
                    .id(1L).ticketNumber("TKT-0001")
                    .customerName("John Doe").customerEmail("john@example.com")
                    .subject("Test subject").body("Test body content")
                    .category("GENERAL").status("IN_PROGRESS").priority("MEDIUM")
                    .build()
        );

        mockMvc.perform(patch("/api/support/tickets/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Should reject blank status - returns 400")
    void updateStatus_shouldReturn400_whenStatusIsBlank() throws Exception {
        TicketStatusUpdateRequestDTO request = TicketStatusUpdateRequestDTO.builder()
                .status("")
                .build();

        mockMvc.perform(patch("/api/support/tickets/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Status is required")));
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete ticket - 204 No Content")
    void deleteTicket_shouldReturn204_whenFound() throws Exception {
        doNothing().when(ticketService).deleteTicket(1L);

        mockMvc.perform(delete("/api/support/tickets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent ticket")
    void deleteTicket_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Support ticket not found with id: 99"))
                .when(ticketService).deleteTicket(99L);

        mockMvc.perform(delete("/api/support/tickets/99"))
                .andExpect(status().isNotFound());
    }

    // ─── Escalation Queue ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return escalation queue - 200 OK")
    void getEscalationQueue_shouldReturn200() throws Exception {
        when(ticketService.getEscalationQueue()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/support/tickets/escalation-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
