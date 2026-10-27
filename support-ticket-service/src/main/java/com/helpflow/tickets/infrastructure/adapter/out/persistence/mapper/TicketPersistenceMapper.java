package com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper;

import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.valueobject.SlaDeadline;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TicketEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketPersistenceMapper {

    @Mapping(target = "id", expression = "java(entity.getId() != null ? new TicketId(entity.getId()) : null)")
    @Mapping(target = "requesterId", expression = "java(entity.getRequester() != null && entity.getRequester().getId() != null ? new UserId(entity.getRequester().getId()) : null)")
    @Mapping(target = "slaDeadline", expression = "java(entity.getSlaDeadline() != null ? new SlaDeadline(entity.getSlaDeadline()) : null)")
    @Mapping(target = "assignedTechnicianId", expression = "java(entity.getAssignedTechnician() != null && entity.getAssignedTechnician().getId() != null ? new TechnicianId(entity.getAssignedTechnician().getId()) : null)")
    Ticket toDomain(TicketEntity entity);

    @Mapping(target = "id", expression = "java(domain.getId() != null ? domain.getId().value() : null)")
    @Mapping(target = "slaDeadline", expression = "java(domain.getSlaDeadline() != null ? domain.getSlaDeadline().value() : null)")
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "assignedTechnician", ignore = true)
    TicketEntity toEntity(Ticket domain);
}
