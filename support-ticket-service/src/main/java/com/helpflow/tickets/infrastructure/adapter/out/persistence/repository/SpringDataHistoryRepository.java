package com.helpflow.tickets.infrastructure.adapter.out.persistence.repository;

import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.AssignmentHistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataHistoryRepository extends JpaRepository<AssignmentHistoryEntity, Long> {

    @EntityGraph(attributePaths = {"actor", "ticket"})
    List<AssignmentHistoryEntity> findByTicketIdOrderByOccurredAtAsc(Long ticketId);
}
