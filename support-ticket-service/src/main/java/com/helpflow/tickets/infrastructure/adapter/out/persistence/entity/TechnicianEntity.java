package com.helpflow.tickets.infrastructure.adapter.out.persistence.entity;

import com.helpflow.tickets.domain.model.TicketCategory;
import com.helpflow.tickets.domain.model.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "technicians")
public class TechnicianEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketCategory specialty;

    @Column(nullable = false, length = 100)
    private String site;

    @Column(name = "max_active_tickets", nullable = false)
    private int maxActiveTickets;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    public TechnicianEntity() {
    }

    public TechnicianEntity(Long id, String code, UserEntity user, String name,
                            TicketCategory specialty, String site, int maxActiveTickets, UserStatus status) {
        this.id = id;
        this.code = code;
        this.user = user;
        this.name = name;
        this.specialty = specialty;
        this.site = site;
        this.maxActiveTickets = maxActiveTickets;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TicketCategory getSpecialty() { return specialty; }
    public void setSpecialty(TicketCategory specialty) { this.specialty = specialty; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public int getMaxActiveTickets() { return maxActiveTickets; }
    public void setMaxActiveTickets(int maxActiveTickets) { this.maxActiveTickets = maxActiveTickets; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
