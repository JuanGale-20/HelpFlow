package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.exception.TicketAccessDeniedException;
import com.helpflow.tickets.application.port.input.ConfigureSlaCommand;
import com.helpflow.tickets.application.port.input.ManageSlaUseCase;
import com.helpflow.tickets.application.port.output.SlaConfigurationPort;
import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.service.SlaPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManageSlaService implements ManageSlaUseCase {

    private final SlaConfigurationPort slaConfigurationPort;

    public ManageSlaService(SlaConfigurationPort slaConfigurationPort) {
        this.slaConfigurationPort = slaConfigurationPort;
    }

    @Override
    public SlaPolicy getCurrentPolicy() {
        return slaConfigurationPort.currentPolicy();
    }

    @Override
    @Transactional
    public SlaPolicy configurePolicy(ConfigureSlaCommand command) {
        if (command.actor().role() != Role.ROLE_ADMIN) {
            throw new TicketAccessDeniedException("Solo un administrador puede configurar las políticas de SLA");
        }

        SlaPolicy newPolicy = new SlaPolicy(command.durations());
        slaConfigurationPort.save(newPolicy);
        return newPolicy;
    }
}
