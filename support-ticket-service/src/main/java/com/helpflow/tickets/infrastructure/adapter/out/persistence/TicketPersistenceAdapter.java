package com.helpflow.tickets.infrastructure.adapter.out.persistence;

import com.helpflow.tickets.application.port.output.TicketRepositoryPort;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.model.TicketStatus;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TicketEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper.TicketPersistenceMapper;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataTechnicianRepository;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataTicketRepository;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TicketPersistenceAdapter implements TicketRepositoryPort {

    private final SpringDataTicketRepository repository;
    private final SpringDataUserRepository userRepository;
    private final SpringDataTechnicianRepository technicianRepository;
    private final TicketPersistenceMapper mapper;

    public TicketPersistenceAdapter(SpringDataTicketRepository repository,
                                  SpringDataUserRepository userRepository,
                                  SpringDataTechnicianRepository technicianRepository,
                                  TicketPersistenceMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.mapper = mapper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = mapper.toEntity(ticket);
        if (ticket.getRequesterId() != null && ticket.getRequesterId().value() != null) {
            UserEntity requester = userRepository.getReferenceById(ticket.getRequesterId().value());
            entity.setRequester(requester);
        }
        if (ticket.getAssignedTechnicianId() != null && ticket.getAssignedTechnicianId().value() != null) {
            TechnicianEntity technician = technicianRepository.getReferenceById(ticket.getAssignedTechnicianId().value());
            entity.setAssignedTechnician(technician);
        } else {
            entity.setAssignedTechnician(null);
        }
        TicketEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(TicketId id) {
        if (id == null || id.value() == null) return Optional.empty();
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public long countActiveByTechnician(TechnicianId technicianId) {
        if (technicianId == null || technicianId.value() == null) return 0;
        return repository.countByAssignedTechnicianIdAndStatusIn(
                technicianId.value(), List.of(TicketStatus.ASIGNADO, TicketStatus.EN_PROGRESO));
    }

    @Override
    public List<Ticket> findWithSlaRunning() {
        return repository.findWithSlaRunning().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findByRequesterId(UserId requesterId) {
        if (requesterId == null || requesterId.value() == null) return List.of();
        return repository.findByRequesterId(requesterId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findByAssignedTechnicianId(TechnicianId technicianId) {
        if (technicianId == null || technicianId.value() == null) return List.of();
        return repository.findByAssignedTechnicianId(technicianId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
