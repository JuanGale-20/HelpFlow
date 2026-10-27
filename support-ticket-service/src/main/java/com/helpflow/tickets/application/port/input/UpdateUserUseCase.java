package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.User;

public interface UpdateUserUseCase {

    User update(UpdateUserCommand command);
}
