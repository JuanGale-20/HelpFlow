package com.helpflow.tickets.infrastructure.adapter.out.persistence.entity;

import com.helpflow.tickets.domain.model.TicketStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "assignment_history")
public class AssignmentHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private TicketStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private TicketStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private UserEntity actor;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    public AssignmentHistoryEntity() {
    }

    public AssignmentHistoryEntity(Long id, TicketEntity ticket, TicketStatus previousStatus,
                                   TicketStatus newStatus, UserEntity actor, String observation, Instant occurredAt) {
        this.id = id;
        this.ticket = ticket;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.actor = actor;
        this.observation = observation;
        this.occurredAt = occurredAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TicketEntity getTicket() { return ticket; }
    public void setTicket(TicketEntity ticket) { this.ticket = ticket; }

    public TicketStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(TicketStatus previousStatus) { this.previousStatus = previousStatus; }

    public TicketStatus getNewStatus() { return newStatus; }
    public void setNewStatus(TicketStatus newStatus) { this.newStatus = newStatus; }

    public UserEntity getActor() { return actor; }
    public void setActor(UserEntity actor) { this.actor = actor; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
}
