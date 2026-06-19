package com.mithulram.assetaccess.repository;

import com.mithulram.assetaccess.domain.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, UUID> {
    List<AuditEventEntity> findTop50ByOrderByOccurredAtDesc();
}
