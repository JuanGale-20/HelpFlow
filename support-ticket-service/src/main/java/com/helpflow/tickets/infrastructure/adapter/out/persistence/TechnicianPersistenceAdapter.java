package com.helpflow.tickets.infrastructure.adapter.out.persistence;

import com.helpflow.tickets.application.port.output.TechnicianRepositoryPort;
import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.model.UserStatus;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.UserId;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.mapper.TechnicianPersistenceMapper;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataTechnicianRepository;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TechnicianPersistenceAdapter implements TechnicianRepositoryPort {

    private final SpringDataTechnicianRepository repository;
    private final SpringDataUserRepository userRepository;
    private final TechnicianPersistenceMapper mapper;

    public TechnicianPersistenceAdapter(SpringDataTechnicianRepository repository,
                                        SpringDataUserRepository userRepository,
                                        TechnicianPersistenceMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Technician save(Technician technician) {
        TechnicianEntity entity = mapper.toEntity(technician);
        if (technician.userId() != null && technician.userId().value() != null) {
            UserEntity user = userRepository.getReferenceById(technician.userId().value());
            entity.setUser(user);
        }
        TechnicianEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Technician> findById(TechnicianId id) {
        if (id == null || id.value() == null) return Optional.empty();
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Technician> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<Technician> findAllActive() {
        return repository.findByStatus(UserStatus.ACTIVO).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Technician> findByUserId(UserId userId) {
        if (userId == null || userId.value() == null) return Optional.empty();
        return repository.findByUserId(userId.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }
}
