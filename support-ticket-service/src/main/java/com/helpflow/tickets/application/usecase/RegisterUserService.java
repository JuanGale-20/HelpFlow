package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.port.input.RegisterUserCommand;
import com.helpflow.tickets.application.port.input.RegisterUserUseCase;
import com.helpflow.tickets.application.port.output.PasswordEncoderPort;
import com.helpflow.tickets.application.port.output.TechnicianRepositoryPort;
import com.helpflow.tickets.application.port.output.UserRepositoryPort;
import com.helpflow.tickets.domain.exception.DomainException;
import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.domain.model.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final TechnicianRepositoryPort technicianRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUserService(UserRepositoryPort userRepository,
                               TechnicianRepositoryPort technicianRepository,
                               PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(RegisterUserCommand command) {
        if (userRepository.existsByDocument(command.document())) {
            throw new DomainException("Ya existe un usuario registrado con el documento " + command.document());
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new DomainException("Ya existe un usuario registrado con el correo " + command.email());
        }

        String passwordHash = passwordEncoder.encode(command.rawPassword());
        User user = User.register(
                command.document(),
                command.name(),
                command.email(),
                passwordHash,
                command.site(),
                command.area(),
                command.role()
        );
        User savedUser = userRepository.save(user);

        if (command.role() == Role.ROLE_TECNICO) {
            if (command.technicianCode() == null || command.technicianCode().isBlank()) {
                throw new DomainException("El código de técnico es obligatorio para el rol ROLE_TECNICO");
            }
            if (command.specialty() == null) {
                throw new DomainException("La especialidad es obligatoria para el rol ROLE_TECNICO");
            }
            if (technicianRepository.existsByCode(command.technicianCode())) {
                throw new DomainException("Ya existe un técnico registrado con el código " + command.technicianCode());
            }

            int maxActive = command.maxActiveTickets() != null && command.maxActiveTickets() > 0
                    ? command.maxActiveTickets()
                    : 5;

            Technician technician = new Technician(
                    null,
                    command.technicianCode(),
                    savedUser.id(),
                    savedUser.name(),
                    command.specialty(),
                    savedUser.site(),
                    maxActive,
                    UserStatus.ACTIVO
            );
            technicianRepository.save(technician);
        }

        return savedUser;
    }
}
