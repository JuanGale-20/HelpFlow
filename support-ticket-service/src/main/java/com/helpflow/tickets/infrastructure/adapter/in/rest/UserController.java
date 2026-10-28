package com.helpflow.tickets.infrastructure.adapter.in.rest;

import com.helpflow.tickets.application.port.input.UpdateUserCommand;
import com.helpflow.tickets.application.port.input.UpdateUserUseCase;
import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.domain.valueobject.UserId;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.UpdateUserRequest;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.UserResponse;
import com.helpflow.tickets.infrastructure.adapter.in.rest.security.SecurityActorResolver;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UpdateUserUseCase updateUserUseCase;
    private final SecurityActorResolver actorResolver;

    public UserController(UpdateUserUseCase updateUserUseCase, SecurityActorResolver actorResolver) {
        this.updateUserUseCase = updateUserUseCase;
        this.actorResolver = actorResolver;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserCommand command = new UpdateUserCommand(
                new UserId(id), request.name(), request.site(), request.area(),
                actorResolver.resolveCurrentActor());

        User user = updateUserUseCase.update(command);
        return ResponseEntity.ok(new UserResponse(
                user.id().value(), user.document(), user.name(), user.email(),
                user.site(), user.area(), user.status(), user.role()));
    }
}
