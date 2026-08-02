package com.epam.taskflow.taskflow_api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    @Builder.Default
    private String category = "GENERAL";

    @Column(nullable = false)
    @Builder.Default
    private String status = "OPEN";

    @Column(nullable = false)
    @Builder.Default
    private String priority = "MEDIUM";

    @Column
    private Integer sentimentScore;

    @Column
    private String sentimentLabel;

    @Column
    @Builder.Default
    private Boolean escalationRequired = false;

    @Column(nullable = false)
    @Builder.Default
    private String source = "EMAIL";

    @Column
    private String assignedAgent;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
