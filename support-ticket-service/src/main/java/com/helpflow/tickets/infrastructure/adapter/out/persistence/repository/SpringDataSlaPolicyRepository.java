package com.helpflow.tickets.infrastructure.adapter.out.persistence.repository;

import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.SlaPolicyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSlaPolicyRepository extends JpaRepository<SlaPolicyEntity, Long> {

    Optional<SlaPolicyEntity> findByPriority(TicketPriority priority);
}
