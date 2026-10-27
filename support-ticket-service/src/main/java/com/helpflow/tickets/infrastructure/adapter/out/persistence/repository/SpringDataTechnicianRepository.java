package com.helpflow.tickets.infrastructure.adapter.out.persistence.repository;

import com.helpflow.tickets.domain.model.UserStatus;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.TechnicianEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTechnicianRepository extends JpaRepository<TechnicianEntity, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<TechnicianEntity> findByCode(String code);

    @EntityGraph(attributePaths = {"user"})
    List<TechnicianEntity> findByStatus(UserStatus status);

    @EntityGraph(attributePaths = {"user"})
    Optional<TechnicianEntity> findByUserId(Long userId);

    boolean existsByCode(String code);
}
