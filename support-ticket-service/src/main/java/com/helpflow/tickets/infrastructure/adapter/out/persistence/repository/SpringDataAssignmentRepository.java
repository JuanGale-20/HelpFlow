package com.helpflow.tickets.infrastructure.adapter.out.persistence.repository;

import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.AssignmentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAssignmentRepository extends JpaRepository<AssignmentEntity, Long> {

    List<AssignmentEntity> findByTicketId(Long ticketId);
}
