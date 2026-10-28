package com.helpflow.tickets.infrastructure.adapter.in.rest;

import com.helpflow.tickets.application.port.input.RegisterUserCommand;
import com.helpflow.tickets.application.port.input.RegisterUserUseCase;
import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.RegisterRequest;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Solo ROLE_ADMIN (protegido por /admin/** en SecurityConfig). */
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final RegisterUserUseCase registerUserUseCase;

    public AdminUserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.document(), request.name(), request.email(), request.password(),
                request.site(), request.area(), request.role(),
                request.technicianCode(), request.specialty(), request.maxActiveTickets());

        User user = registerUserUseCase.register(command);
        UserResponse response = new UserResponse(
                user.id().value(), user.document(), user.name(), user.email(),
                user.site(), user.area(), user.status(), user.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
