package com.helpflow.tickets.application.port.output;

import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.util.List;
import java.util.Optional;

public interface TicketRepositoryPort {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(TicketId id);

    /** Tickets ASIGNADO o EN_PROGRESO de un técnico (su carga activa). */
    long countActiveByTechnician(TechnicianId technicianId);

    /** Tickets con el reloj de SLA corriendo (para el proceso de escalamiento). */
    List<Ticket> findWithSlaRunning();

    List<Ticket> findAll();

    List<Ticket> findByRequesterId(UserId requesterId);

    List<Ticket> findByAssignedTechnicianId(TechnicianId technicianId);
}
