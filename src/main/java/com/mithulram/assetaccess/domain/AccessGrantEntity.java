package com.mithulram.assetaccess.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "access_grants")
public class AccessGrantEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID assetId;

    @Column(nullable = false, length = 120)
    private String principal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetPermission permission;

    @Column(nullable = false)
    private Instant grantedAt;

    protected AccessGrantEntity() {
    }

    public AccessGrantEntity(UUID id, UUID assetId, String principal, AssetPermission permission, Instant grantedAt) {
        this.id = id;
        this.assetId = assetId;
        this.principal = principal;
        this.permission = permission;
        this.grantedAt = grantedAt;
    }

    public UUID getId() { return id; }
    public UUID getAssetId() { return assetId; }
    public String getPrincipal() { return principal; }
    public AssetPermission getPermission() { return permission; }
    public Instant getGrantedAt() { return grantedAt; }
}
