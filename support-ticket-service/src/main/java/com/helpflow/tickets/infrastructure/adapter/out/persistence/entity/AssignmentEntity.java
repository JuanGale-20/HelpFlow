package com.helpflow.tickets.infrastructure.adapter.out.persistence.entity;

import com.helpflow.tickets.domain.model.Availability;
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
@Table(name = "assignments")
public class AssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false)
    private TechnicianEntity technician;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Availability availability;

    public AssignmentEntity() {
    }

    public AssignmentEntity(Long id, TicketEntity ticket, TechnicianEntity technician,
                            Instant assignedAt, Integer estimatedHours, Availability availability) {
        this.id = id;
        this.ticket = ticket;
        this.technician = technician;
        this.assignedAt = assignedAt;
        this.estimatedHours = estimatedHours;
        this.availability = availability;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TicketEntity getTicket() { return ticket; }
    public void setTicket(TicketEntity ticket) { this.ticket = ticket; }

    public TechnicianEntity getTechnician() { return technician; }
    public void setTechnician(TechnicianEntity technician) { this.technician = technician; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }

    public Integer getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }

    public Availability getAvailability() { return availability; }
    public void setAvailability(Availability availability) { this.availability = availability; }
}
