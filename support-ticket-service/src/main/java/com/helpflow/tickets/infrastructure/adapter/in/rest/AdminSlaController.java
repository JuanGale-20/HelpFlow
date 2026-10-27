package com.helpflow.tickets.infrastructure.adapter.in.rest;

import com.helpflow.tickets.application.port.input.Actor;
import com.helpflow.tickets.application.port.input.ConfigureSlaCommand;
import com.helpflow.tickets.application.port.input.ManageSlaUseCase;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.service.SlaPolicy;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.ConfigureSlaRequest;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.SlaPolicyResponse;
import com.helpflow.tickets.infrastructure.adapter.in.rest.security.SecurityActorResolver;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/sla")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminSlaController {

    private final ManageSlaUseCase manageSlaUseCase;
    private final SecurityActorResolver actorResolver;

    public AdminSlaController(ManageSlaUseCase manageSlaUseCase,
                              SecurityActorResolver actorResolver) {
        this.manageSlaUseCase = manageSlaUseCase;
        this.actorResolver = actorResolver;
    }

    @GetMapping
    public ResponseEntity<SlaPolicyResponse> getCurrentPolicy() {
        SlaPolicy policy = manageSlaUseCase.getCurrentPolicy();
        return ResponseEntity.ok(SlaPolicyResponse.fromDomain(policy));
    }

    @PutMapping
    public ResponseEntity<SlaPolicyResponse> configurePolicy(@Valid @RequestBody ConfigureSlaRequest request) {
        Actor actor = actorResolver.resolveCurrentActor();

        Map<TicketPriority, Duration> durations = new EnumMap<>(TicketPriority.class);
        request.hours().forEach((priority, hours) -> durations.put(priority, Duration.ofHours(hours)));

        ConfigureSlaCommand command = new ConfigureSlaCommand(durations, actor);
        SlaPolicy updated = manageSlaUseCase.configurePolicy(command);
        return ResponseEntity.ok(SlaPolicyResponse.fromDomain(updated));
    }
}
