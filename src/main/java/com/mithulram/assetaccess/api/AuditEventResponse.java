package com.mithulram.assetaccess.api;

import com.mithulram.assetaccess.domain.AuditEventEntity;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(UUID id, String actor, String action, String target, Instant occurredAt) {
    public static AuditEventResponse from(AuditEventEntity event) {
        return new AuditEventResponse(event.getId(), event.getActor(), event.getAction(), event.getTarget(), event.getOccurredAt());
    }
}
