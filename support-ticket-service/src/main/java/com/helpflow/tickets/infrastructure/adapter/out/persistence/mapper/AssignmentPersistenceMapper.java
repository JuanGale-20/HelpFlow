package com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper;

import com.helpflow.tickets.domain.model.Assignment;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.AssignmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentPersistenceMapper {

    @Mapping(target = "ticketId", expression = "java(entity.getTicket() != null && entity.getTicket().getId() != null ? new TicketId(entity.getTicket().getId()) : null)")
    @Mapping(target = "technicianId", expression = "java(entity.getTechnician() != null && entity.getTechnician().getId() != null ? new TechnicianId(entity.getTechnician().getId()) : null)")
    Assignment toDomain(AssignmentEntity entity);

    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "technician", ignore = true)
    AssignmentEntity toEntity(Assignment domain);
}
