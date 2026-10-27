package com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper;

import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.UserId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TechnicianPersistenceMapper {

    @Mapping(target = "id", expression = "java(entity.getId() != null ? new TechnicianId(entity.getId()) : null)")
    @Mapping(target = "userId", expression = "java(entity.getUser() != null && entity.getUser().getId() != null ? new UserId(entity.getUser().getId()) : null)")
    Technician toDomain(TechnicianEntity entity);

    @Mapping(target = "id", expression = "java(domain.id() != null ? domain.id().value() : null)")
    @Mapping(target = "user", ignore = true)
    TechnicianEntity toEntity(Technician domain);
}
