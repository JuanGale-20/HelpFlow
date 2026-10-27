package com.helpflow.tickets.infrastructure.adapter.out.persistence;

import com.helpflow.tickets.application.port.output.AssignmentRepositoryPort;
import com.helpflow.tickets.domain.model.Assignment;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.AssignmentEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TicketEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper.AssignmentPersistenceMapper;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataAssignmentRepository;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataTechnicianRepository;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataTicketRepository;
import org.springframework.stereotype.Component;

@Component
public class AssignmentPersistenceAdapter implements AssignmentRepositoryPort {

    private final SpringDataAssignmentRepository repository;
    private final SpringDataTicketRepository ticketRepository;
    private final SpringDataTechnicianRepository technicianRepository;
    private final AssignmentPersistenceMapper mapper;

    public AssignmentPersistenceAdapter(SpringDataAssignmentRepository repository,
                                        SpringDataTicketRepository ticketRepository,
                                        SpringDataTechnicianRepository technicianRepository,
                                        AssignmentPersistenceMapper mapper) {
        this.repository = repository;
        this.ticketRepository = ticketRepository;
        this.technicianRepository = technicianRepository;
        this.mapper = mapper;
    }

    @Override
    public Assignment save(Assignment assignment) {
        AssignmentEntity entity = mapper.toEntity(assignment);
        TicketEntity ticket = ticketRepository.getReferenceById(assignment.ticketId().value());
        TechnicianEntity technician = technicianRepository.getReferenceById(assignment.technicianId().value());
        entity.setTicket(ticket);
        entity.setTechnician(technician);

        AssignmentEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
}
