package com.helpflow.tickets.infrastructure.adapter.out.persistence.entity;

import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.TicketPriority;
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
@Table(name = "tickets")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private UserEntity requester;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPriority priority;

    @Column(nullable = false, length = 100)
    private String site;

    @Column(name = "affected_asset", length = 150)
    private String affectedAsset;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sla_deadline", nullable = false)
    private Instant slaDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician_id")
    private TechnicianEntity assignedTechnician;

    @Column(name = "first_response_at")
    private Instant firstResponseAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "escalation_count", nullable = false)
    private int escalationCount = 0;

    public TicketEntity() {
    }

    public TicketEntity(Long id, UserEntity requester, String title, String description,
                        TicketCategory category, TicketPriority priority, String site,
                        String affectedAsset, Instant createdAt, Instant slaDeadline,
                        TicketStatus status, TechnicianEntity assignedTechnician,
                        Instant firstResponseAt, Instant resolvedAt, int escalationCount) {
        this.id = id;
        this.requester = requester;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.site = site;
        this.affectedAsset = affectedAsset;
        this.createdAt = createdAt;
        this.slaDeadline = slaDeadline;
        this.status = status;
        this.assignedTechnician = assignedTechnician;
        this.firstResponseAt = firstResponseAt;
        this.resolvedAt = resolvedAt;
        this.escalationCount = escalationCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserEntity getRequester() { return requester; }
    public void setRequester(UserEntity requester) { this.requester = requester; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TicketCategory getCategory() { return category; }
    public void setCategory(TicketCategory category) { this.category = category; }

    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public String getAffectedAsset() { return affectedAsset; }
    public void setAffectedAsset(String affectedAsset) { this.affectedAsset = affectedAsset; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(Instant slaDeadline) { this.slaDeadline = slaDeadline; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public TechnicianEntity getAssignedTechnician() { return assignedTechnician; }
    public void setAssignedTechnician(TechnicianEntity assignedTechnician) { this.assignedTechnician = assignedTechnician; }

    public Instant getFirstResponseAt() { return firstResponseAt; }
    public void setFirstResponseAt(Instant firstResponseAt) { this.firstResponseAt = firstResponseAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }

    public int getEscalationCount() { return escalationCount; }
    public void setEscalationCount(int escalationCount) { this.escalationCount = escalationCount; }
}
