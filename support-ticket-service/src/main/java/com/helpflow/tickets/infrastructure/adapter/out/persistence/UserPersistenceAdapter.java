package com.helpflow.tickets.infrastructure.adapter.out.persistence;

import com.helpflow.tickets.application.port.output.UserRepositoryPort;
import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.domain.valueobject.UserId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(SpringDataUserRepository repository, UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        if (id == null || id.value() == null) return Optional.empty();
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByDocument(String document) {
        return repository.findByDocument(document).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByDocument(String document) {
        return repository.existsByDocument(document);
    }
}
