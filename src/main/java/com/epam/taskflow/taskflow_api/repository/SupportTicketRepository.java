package com.epam.taskflow.taskflow_api.repository;

import com.epam.taskflow.taskflow_api.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByStatus(String status);

    List<SupportTicket> findByCategory(String category);

    List<SupportTicket> findByPriority(String priority);

    List<SupportTicket> findByEscalationRequired(Boolean escalationRequired);

    Optional<SupportTicket> findByTicketNumber(String ticketNumber);

    long countByStatus(String status);

    long countByPriority(String priority);

    long countByCategory(String category);
}
