package com.helpflow.tickets.application.port.output;

import com.helpflow.tickets.domain.model.Assignment;

public interface AssignmentRepositoryPort {

    Assignment save(Assignment assignment);
}