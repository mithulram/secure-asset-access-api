package com.mithulram.assetaccess.service;

import com.mithulram.assetaccess.api.CreateAccessGrantRequest;
import com.mithulram.assetaccess.api.CreateAssetRequest;
import com.mithulram.assetaccess.domain.AccessGrantEntity;
import com.mithulram.assetaccess.domain.AssetEntity;
import com.mithulram.assetaccess.domain.AuditEventEntity;
import com.mithulram.assetaccess.repository.AccessGrantRepository;
import com.mithulram.assetaccess.repository.AssetRepository;
import com.mithulram.assetaccess.repository.AuditEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AssetAccessService {
    private final AssetRepository assets;
    private final AccessGrantRepository accessGrants;
    private final AuditEventRepository auditEvents;
    private final Clock clock;

    @Autowired
    public AssetAccessService(
            AssetRepository assets,
            AccessGrantRepository accessGrants,
            AuditEventRepository auditEvents) {
        this(assets, accessGrants, auditEvents, Clock.systemUTC());
    }

    AssetAccessService(
            AssetRepository assets,
            AccessGrantRepository accessGrants,
            AuditEventRepository auditEvents,
            Clock clock) {
        this.assets = assets;
        this.accessGrants = accessGrants;
        this.auditEvents = auditEvents;
        this.clock = clock;
    }

    @Transactional
    public AssetEntity createAsset(CreateAssetRequest request, String actor) {
        Instant now = clock.instant();
        AssetEntity asset = assets.save(new AssetEntity(
                UUID.randomUUID(), request.name().trim(), request.ownerTeam().trim(), request.classification(), now));
        appendAudit(actor, "ASSET_CREATED", asset.getId().toString(), now);
        return asset;
    }

    @Transactional(readOnly = true)
    public List<AssetEntity> listAssets() {
        return assets.findAll().stream().sorted((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt())).toList();
    }

    @Transactional(readOnly = true)
    public AssetEntity getAsset(UUID assetId) {
        return assets.findById(assetId).orElseThrow(() -> new AssetNotFoundException(assetId));
    }

    @Transactional
    public AccessGrantEntity grantAccess(UUID assetId, CreateAccessGrantRequest request, String actor) {
        getAsset(assetId);
        Instant now = clock.instant();
        AccessGrantEntity grant = accessGrants.save(new AccessGrantEntity(
                UUID.randomUUID(), assetId, request.principal().trim(), request.permission(), now));
        appendAudit(actor, "ACCESS_GRANTED", assetId + ":" + grant.getPrincipal(), now);
        return grant;
    }

    @Transactional(readOnly = true)
    public List<AccessGrantEntity> listAccessGrants(UUID assetId) {
        getAsset(assetId);
        return accessGrants.findByAssetIdOrderByGrantedAtDesc(assetId);
    }

    @Transactional(readOnly = true)
    public List<AuditEventEntity> listAuditEvents() {
        return auditEvents.findTop50ByOrderByOccurredAtDesc();
    }

    private void appendAudit(String actor, String action, String target, Instant occurredAt) {
        auditEvents.save(new AuditEventEntity(UUID.randomUUID(), actor, action, target, occurredAt));
    }
}
