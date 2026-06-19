package com.mithulram.assetaccess.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEventEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String actor;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false, length = 120)
    private String target;

    @Column(nullable = false)
    private Instant occurredAt;

    protected AuditEventEntity() {
    }

    public AuditEventEntity(UUID id, String actor, String action, String target, Instant occurredAt) {
        this.id = id;
        this.actor = actor;
        this.action = action;
        this.target = target;
        this.occurredAt = occurredAt;
    }

    public UUID getId() { return id; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getTarget() { return target; }
    public Instant getOccurredAt() { return occurredAt; }
}
