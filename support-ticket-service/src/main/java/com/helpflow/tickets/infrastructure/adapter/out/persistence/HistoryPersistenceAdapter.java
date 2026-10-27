package com.helpflow.tickets.infrastructure.adapter.out.persistence;

import com.helpflow.tickets.application.port.output.HistoryRepositoryPort;
import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.AssignmentHistoryEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TicketEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper.AssignmentHistoryPersistenceMapper;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataHistoryRepository;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataTicketRepository;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class HistoryPersistenceAdapter implements HistoryRepositoryPort {

    private final SpringDataHistoryRepository repository;
    private final SpringDataTicketRepository ticketRepository;
    private final SpringDataUserRepository userRepository;
    private final AssignmentHistoryPersistenceMapper mapper;

    public HistoryPersistenceAdapter(SpringDataHistoryRepository repository,
                                   SpringDataTicketRepository ticketRepository,
                                   SpringDataUserRepository userRepository,
                                   AssignmentHistoryPersistenceMapper mapper) {
        this.repository = repository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public AssignmentHistory save(AssignmentHistory history) {
        AssignmentHistoryEntity entity = mapper.toEntity(history);
        TicketEntity ticket = ticketRepository.getReferenceById(history.ticketId().value());
        entity.setTicket(ticket);
        if (history.actorId() != null && history.actorId().value() != null) {
            UserEntity actor = userRepository.getReferenceById(history.actorId().value());
            entity.setActor(actor);
        } else {
            entity.setActor(null);
        }
        AssignmentHistoryEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<AssignmentHistory> findByTicketId(TicketId ticketId) {
        if (ticketId == null || ticketId.value() == null) return List.of();
        return repository.findByTicketIdOrderByOccurredAtAsc(ticketId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
