package com.helpflow.tickets.infrastructure.adapter.out.persistence.entity;

import com.helpflow.tickets.domain.model.TicketPriority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sla_policies")
public class SlaPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private TicketPriority priority;

    @Column(name = "duration_hours", nullable = false)
    private int durationHours;

    public SlaPolicyEntity() {
    }

    public SlaPolicyEntity(Long id, TicketPriority priority, int durationHours) {
        this.id = id;
        this.priority = priority;
        this.durationHours = durationHours;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }
}
