package com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper;

import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.domain.valueobject.UserId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "id", expression = "java(entity.getId() != null ? new UserId(entity.getId()) : null)")
    User toDomain(UserEntity entity);

    @Mapping(target = "id", expression = "java(domain.id() != null ? domain.id().value() : null)")
    @Mapping(target = "createdAt", ignore = true)
    UserEntity toEntity(User domain);

    default Long map(UserId id) {
        return id != null ? id.value() : null;
    }

    default UserId map(Long id) {
        return id != null ? new UserId(id) : null;
    }
}
