package com.helpflow.tickets.domain.model;

import com.helpflow.tickets.domain.exception.DomainException;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.util.regex.Pattern;

public record User(
        UserId id,
        String document,
        String name,
        String email,
        String passwordHash,
        String site,
        String area,
        UserStatus status,
        Role role
) {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public User {
        Preconditions.requireText(document, "El documento es obligatorio");
        Preconditions.requireText(name, "El nombre es obligatorio");
        Preconditions.requireText(email, "El correo es obligatorio");
        if (!EMAIL.matcher(email).matches()) {
            throw new DomainException("El correo corporativo no es válido");
        }
        Preconditions.requireText(passwordHash, "La contraseña es obligatoria");
        Preconditions.requireText(site, "La sede es obligatoria");
        Preconditions.requireText(area, "El área es obligatoria");
        Preconditions.requireNonNull(status, "El estado es obligatorio");
        Preconditions.requireNonNull(role, "El rol es obligatorio");
    }

    public static User register(String document, String name, String email, String passwordHash,
                                String site, String area, Role role) {
        return new User(null, document, name, email, passwordHash, site, area, UserStatus.ACTIVO, role);
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVO;
    }

    public void ensureCanCreateTickets() {
        if (!isActive()) {
            throw new DomainException("Solo un usuario ACTIVO puede registrar tickets");
        }
    }

    public User updateProfile(String name, String site, String area) {
        return new User(id, document, name, email, passwordHash, site, area, status, role);
    }

    public User deactivate() {
        return new User(id, document, name, email, passwordHash, site, area, UserStatus.INACTIVO, role);
    }
}