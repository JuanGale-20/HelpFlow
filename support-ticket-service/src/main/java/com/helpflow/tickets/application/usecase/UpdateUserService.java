package com.helpflow.tickets.application.usecase;

import com.helpflow.tickets.application.exception.ResourceNotFoundException;
import com.helpflow.tickets.application.exception.TicketAccessDeniedException;
import com.helpflow.tickets.application.port.input.UpdateUserCommand;
import com.helpflow.tickets.application.port.input.UpdateUserUseCase;
import com.helpflow.tickets.application.port.output.TechnicianRepositoryPort;
import com.helpflow.tickets.application.port.output.UserRepositoryPort;
import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.model.Technician;
import com.helpflow.tickets.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final TechnicianRepositoryPort technicianRepository;

    public UpdateUserService(UserRepositoryPort userRepository,
                             TechnicianRepositoryPort technicianRepository) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
    }

    @Override
    @Transactional
    public User update(UpdateUserCommand command) {
        boolean isSelf = command.actor().userId().equals(command.userId());
        boolean isAdmin = command.actor().role() == Role.ROLE_ADMIN;

        if (!isSelf && !isAdmin) {
            throw new TicketAccessDeniedException("No tienes permisos para modificar este perfil de usuario");
        }

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario " + command.userId().value()));

        User updatedUser = user.updateProfile(command.name(), command.site(), command.area());
        User savedUser = userRepository.save(updatedUser);

        technicianRepository.findByUserId(command.userId()).ifPresent(technician -> {
            Technician updatedTechnician = new Technician(
                    technician.id(),
                    technician.code(),
                    technician.userId(),
                    command.name(),
                    technician.specialty(),
                    command.site(),
                    technician.maxActiveTickets(),
                    technician.status()
            );
            technicianRepository.save(updatedTechnician);
        });

        return savedUser;
    }
}
