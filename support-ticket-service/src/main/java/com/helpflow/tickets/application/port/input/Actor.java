package com.helpflow.tickets.application.port.input;

import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.valueobject.UserId;

/** Quién ejecuta la acción: lo construye el adaptador REST a partir del JWT. */
public record Actor(UserId userId, Role role) {
}
