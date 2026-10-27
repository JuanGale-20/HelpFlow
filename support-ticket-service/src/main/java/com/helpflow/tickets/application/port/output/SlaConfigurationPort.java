package com.helpflow.tickets.application.port.output;

import com.helpflow.tickets.domain.service.SlaPolicy;

/** De dónde sale la configuración de SLA (la base de datos; el admin puede cambiarla). */
public interface SlaConfigurationPort {

    SlaPolicy currentPolicy();

    void save(SlaPolicy policy);
}
