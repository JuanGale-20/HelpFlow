package com.helpflow.tickets.infrastructure.adapter.out.persistence.repository;

import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TicketEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataTicketRepository extends JpaRepository<TicketEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    List<TicketEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    Optional<TicketEntity> findById(Long id);

    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    List<TicketEntity> findByRequesterId(Long requesterId);

    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    List<TicketEntity> findByAssignedTechnicianId(Long technicianId);

    long countByAssignedTechnicianIdAndStatusIn(Long technicianId, Collection<TicketStatus> statuses);

    @EntityGraph(attributePaths = {"requester", "assignedTechnician"})
    @Query("SELECT t FROM TicketEntity t WHERE t.status IN ('ABIERTO', 'ASIGNADO', 'EN_PROGRESO')")
    List<TicketEntity> findWithSlaRunning();
}
