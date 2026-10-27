package com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper;

import com.helpflow.tickets.domain.model.AssignmentHistory;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.AssignmentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentHistoryPersistenceMapper {

    @Mapping(target = "ticketId", expression = "java(entity.getTicket() != null && entity.getTicket().getId() != null ? new TicketId(entity.getTicket().getId()) : null)")
    @Mapping(target = "actorId", expression = "java(entity.getActor() != null && entity.getActor().getId() != null ? new UserId(entity.getActor().getId()) : null)")
    AssignmentHistory toDomain(AssignmentHistoryEntity entity);

    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "actor", ignore = true)
    AssignmentHistoryEntity toEntity(AssignmentHistory domain);
}
