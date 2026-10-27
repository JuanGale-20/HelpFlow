package com.helpflow.tickets.infrastructure.adapter.in.rest;

import com.helpflow.tickets.application.port.input.Actor;
import com.helpflow.tickets.application.port.input.AssignTicketCommand;
import com.helpflow.tickets.application.port.input.AssignTicketUseCase;
import com.helpflow.tickets.application.port.input.ChangeStatusCommand;
import com.helpflow.tickets.application.port.input.CreateTicketCommand;
import com.helpflow.tickets.application.port.input.CreateTicketUseCase;
import com.helpflow.tickets.application.port.input.ManageTicketUseCase;
import com.helpflow.tickets.application.port.input.QueryTicketsUseCase;
import com.helpflow.tickets.domain.model.Ticket;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.AssignTicketRequest;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.AssignmentHistoryResponse;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.ChangeStatusRequest;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.CreateTicketRequest;
import com.helpflow.tickets.infrastructure.adapter.in.rest.dto.TicketResponse;
import com.helpflow.tickets.infrastructure.adapter.in.rest.security.SecurityActorResolver;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final CreateTicketUseCase createTicketUseCase;
    private final QueryTicketsUseCase queryTicketsUseCase;
    private final ManageTicketUseCase manageTicketUseCase;
    private final AssignTicketUseCase assignTicketUseCase;
    private final SecurityActorResolver actorResolver;

    public TicketController(CreateTicketUseCase createTicketUseCase,
                            QueryTicketsUseCase queryTicketsUseCase,
                            ManageTicketUseCase manageTicketUseCase,
                            AssignTicketUseCase assignTicketUseCase,
                            SecurityActorResolver actorResolver) {
        this.createTicketUseCase = createTicketUseCase;
        this.queryTicketsUseCase = queryTicketsUseCase;
        this.manageTicketUseCase = manageTicketUseCase;
        this.assignTicketUseCase = assignTicketUseCase;
        this.actorResolver = actorResolver;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        Actor actor = actorResolver.resolveCurrentActor();

        CreateTicketCommand command = new CreateTicketCommand(
                actor.userId(),
                request.title(),
                request.description(),
                request.category(),
                request.priority(),
                request.site(),
                request.affectedAsset()
        );

        Ticket ticket = createTicketUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponse.fromDomain(ticket));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        Actor actor = actorResolver.resolveCurrentActor();
        Ticket ticket = queryTicketsUseCase.getById(new TicketId(id), actor);
        return ResponseEntity.ok(TicketResponse.fromDomain(ticket));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> list() {
        Actor actor = actorResolver.resolveCurrentActor();
        List<Ticket> tickets = queryTicketsUseCase.list(actor);
        return ResponseEntity.ok(tickets.stream().map(TicketResponse::fromDomain).toList());
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<AssignmentHistoryResponse>> history(@PathVariable Long id) {
        Actor actor = actorResolver.resolveCurrentActor();
        return ResponseEntity.ok(
                queryTicketsUseCase.history(new TicketId(id), actor).stream()
                        .map(AssignmentHistoryResponse::fromDomain)
                        .toList()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> changeStatus(@PathVariable Long id,
                                                       @Valid @RequestBody ChangeStatusRequest request) {
        Actor actor = actorResolver.resolveCurrentActor();
        ChangeStatusCommand command = new ChangeStatusCommand(
                new TicketId(id),
                request.status(),
                actor,
                request.observation()
        );
        Ticket updated = manageTicketUseCase.changeStatus(command);
        return ResponseEntity.ok(TicketResponse.fromDomain(updated));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TicketResponse> assignManually(@PathVariable Long id,
                                                         @Valid @RequestBody AssignTicketRequest request) {
        Actor actor = actorResolver.resolveCurrentActor();
        AssignTicketCommand command = new AssignTicketCommand(
                new TicketId(id),
                new TechnicianId(request.technicianId()),
                actor.userId(),
                request.observation()
        );
        Ticket updated = assignTicketUseCase.assignManually(command);
        return ResponseEntity.ok(TicketResponse.fromDomain(updated));
    }
}
