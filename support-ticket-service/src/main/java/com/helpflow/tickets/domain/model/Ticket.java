package com.helpflow.tickets.domain.model;

import com.helpflow.tickets.domain.exception.InvalidTicketStateException;
import com.helpflow.tickets.domain.service.SlaPolicy;
import com.helpflow.tickets.domain.service.TicketTransitionPolicy;
import com.helpflow.tickets.domain.valueobject.SlaDeadline;
import com.helpflow.tickets.domain.valueobject.TechnicianId;
import com.helpflow.tickets.domain.valueobject.TicketId;
import com.helpflow.tickets.domain.valueobject.UserId;
import java.time.Instant;

public class Ticket {

    /** Cada ticket se escala automáticamente como máximo una vez. */
    public static final int MAX_AUTOMATIC_ESCALATIONS = 1;

    private static final TicketTransitionPolicy TRANSITIONS = new TicketTransitionPolicy();

    private final TicketId id;
    private final UserId requesterId;
    private final String title;
    private final String description;
    private final TicketCategory category;
    private TicketPriority priority;
    private final String site;
    private final String affectedAsset;
    private final Instant createdAt;
    private final SlaDeadline slaDeadline;
    private TicketStatus status;
    private TechnicianId assignedTechnicianId;
    private Instant firstResponseAt;
    private Instant resolvedAt;
    private int escalationCount;

    /** Constructor completo: lo usa el adaptador de persistencia para reconstruir un ticket guardado. */
    public Ticket(TicketId id, UserId requesterId, String title, String description, TicketCategory category,
                  TicketPriority priority, String site, String affectedAsset, Instant createdAt,
                  SlaDeadline slaDeadline, TicketStatus status, TechnicianId assignedTechnicianId,
                  Instant firstResponseAt, Instant resolvedAt, int escalationCount) {
        Preconditions.requireNonNull(requesterId, "El usuario solicitante es obligatorio");
        Preconditions.requireText(title, "El título es obligatorio");
        Preconditions.requireText(description, "La descripción es obligatoria");
        Preconditions.requireNonNull(category, "La categoría es obligatoria");
        Preconditions.requireNonNull(priority, "La prioridad es obligatoria");
        Preconditions.requireText(site, "La sede es obligatoria");
        Preconditions.requireNonNull(createdAt, "La fecha de creación es obligatoria");
        Preconditions.requireNonNull(slaDeadline, "La fecha límite de SLA es obligatoria");
        Preconditions.requireNonNull(status, "El estado es obligatorio");
        this.id = id;
        this.requesterId = requesterId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.site = site;
        this.affectedAsset = affectedAsset;
        this.createdAt = createdAt;
        this.slaDeadline = slaDeadline;
        this.status = status;
        this.assignedTechnicianId = assignedTechnicianId;
        this.firstResponseAt = firstResponseAt;
        this.resolvedAt = resolvedAt;
        this.escalationCount = escalationCount;
    }

    public static Ticket create(UserId requesterId, String title, String description, TicketCategory category,
                                TicketPriority priority, String site, String affectedAsset,
                                SlaDeadline slaDeadline, Instant now) {
        return new Ticket(null, requesterId, title, description, category, priority, site, affectedAsset,
                now, slaDeadline, TicketStatus.ABIERTO, null, null, null, 0);
    }

    /** Primera asignación: el ticket debe estar ABIERTO. */
    public void assignTo(TechnicianId technicianId) {
        Preconditions.requireNonNull(technicianId, "El técnico es obligatorio");
        TRANSITIONS.validate(status, TicketStatus.ASIGNADO);
        this.assignedTechnicianId = technicianId;
        this.status = TicketStatus.ASIGNADO;
    }

    /** Reasignación (admin): solo para tickets ASIGNADO o EN_PROGRESO. */
    public void reassignTo(TechnicianId technicianId) {
        Preconditions.requireNonNull(technicianId, "El técnico es obligatorio");
        if (status != TicketStatus.ASIGNADO && status != TicketStatus.EN_PROGRESO) {
            throw new InvalidTicketStateException("Solo se puede reasignar un ticket ASIGNADO o EN_PROGRESO");
        }
        this.assignedTechnicianId = technicianId;
        this.status = TicketStatus.ASIGNADO;
    }

    public void changeStatus(TicketStatus newStatus, Instant now) {
        if (newStatus == TicketStatus.ASIGNADO) {
            throw new InvalidTicketStateException("El estado ASIGNADO solo se alcanza asignando un técnico");
        }
        TRANSITIONS.validate(status, newStatus);
        if (newStatus == TicketStatus.EN_PROGRESO && firstResponseAt == null) {
            firstResponseAt = now;
        }
        if (newStatus == TicketStatus.RESUELTO) {
            resolvedAt = now;
        }
        if (status == TicketStatus.RESUELTO && newStatus == TicketStatus.EN_PROGRESO) {
            resolvedAt = null;
        }
        this.status = newStatus;
    }

    public boolean isOverdue(Instant now) {
        return status.isSlaRunning() && slaDeadline.isExpired(now);
    }

    public boolean isNearSlaBreach(Instant now) {
        return status.isSlaRunning()
                && slaDeadline.consumedRatio(createdAt, now) >= SlaPolicy.ESCALATION_THRESHOLD;
    }

    public boolean canBeEscalated(Instant now) {
        return isNearSlaBreach(now) && !priority.isMax() && escalationCount < MAX_AUTOMATIC_ESCALATIONS;
    }

    public void escalate() {
        if (priority.isMax()) {
            throw new InvalidTicketStateException("El ticket ya tiene la prioridad máxima");
        }
        this.priority = priority.escalate();
        this.escalationCount++;
    }

    public boolean isRequestedBy(UserId userId) {
        return requesterId.equals(userId);
    }

    public boolean isAssignedTo(TechnicianId technicianId) {
        return assignedTechnicianId != null && assignedTechnicianId.equals(technicianId);
    }

    public TicketId getId() { return id; }
    public UserId getRequesterId() { return requesterId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketCategory getCategory() { return category; }
    public TicketPriority getPriority() { return priority; }
    public String getSite() { return site; }
    public String getAffectedAsset() { return affectedAsset; }
    public Instant getCreatedAt() { return createdAt; }
    public SlaDeadline getSlaDeadline() { return slaDeadline; }
    public TicketStatus getStatus() { return status; }
    public TechnicianId getAssignedTechnicianId() { return assignedTechnicianId; }
    public Instant getFirstResponseAt() { return firstResponseAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public int getEscalationCount() { return escalationCount; }
}