package com.helpflow.tickets.application.port.output;

import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.util.List;
import java.util.Optional;

public interface TechnicianRepositoryPort {

    Technician save(Technician technician);

    Optional<Technician> findById(TechnicianId id);

    /** Busca por código (TEC-014): es lo que devuelve el servicio de despacho. */
    Optional<Technician> findByCode(String code);

    List<Technician> findAllActive();

    Optional<Technician> findByUserId(UserId userId);

    boolean existsByCode(String code);
}
