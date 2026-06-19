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
@Table(name = "assets")
public class AssetEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String ownerTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetClassification classification;

    @Column(nullable = false)
    private Instant createdAt;

    protected AssetEntity() {
    }

    public AssetEntity(UUID id, String name, String ownerTeam, AssetClassification classification, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.ownerTeam = ownerTeam;
        this.classification = classification;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getOwnerTeam() { return ownerTeam; }
    public AssetClassification getClassification() { return classification; }
    public Instant getCreatedAt() { return createdAt; }
}
