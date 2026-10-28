package com.helpflow.tickets.infrastructure.adapter.in.rest;

import com.helpflow.tickets.application.port.input.RegisterUserCommand;
import com.helpflow.tickets.application.port.input.RegisterUserUseCase;
import com.helpflow.tickets.application.port.output.PasswordEncoderPort;
import com.helpflow.tickets.application.port.output.UserRepositoryPort;
import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.AuthResponse;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.PublicRegisterRequest;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.UserResponse;
import com.helpflow.tickets.infrastructure.adapter.in.rest.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtService jwtService;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          UserRepositoryPort userRepository,
                          PasswordEncoderPort passwordEncoder,
                          JwtService jwtService) {
        this.registerUserUseCase = registerUserUseCase;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody PublicRegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.document(),
                request.name(),
                request.email(),
                request.password(),
                request.site(),
                request.area(),
                Role.ROLE_USUARIO,   // el registro público SIEMPRE crea usuarios normales
                null,
                null,
                null
        );

        User user = registerUserUseCase.register(command);
        UserResponse response = new UserResponse(
                user.id().value(),
                user.document(),
                user.name(),
                user.email(),
                user.site(),
                user.area(),
                user.status(),
                user.role()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        if (!user.isActive()) {
            throw new DisabledException("El usuario se encuentra inactivo");
        }

        String token = jwtService.generateToken(user);
        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                user.id().value(),
                user.name(),
                user.email(),
                user.role()
        );

        return ResponseEntity.ok(response);
    }
}
