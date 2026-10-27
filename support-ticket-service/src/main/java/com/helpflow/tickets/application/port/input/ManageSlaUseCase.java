package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.service.SlaPolicy;

public interface ManageSlaUseCase {

    SlaPolicy getCurrentPolicy();

    SlaPolicy configurePolicy(ConfigureSlaCommand command);
}
