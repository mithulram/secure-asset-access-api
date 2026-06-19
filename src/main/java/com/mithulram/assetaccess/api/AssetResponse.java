package com.mithulram.assetaccess.api;

import com.mithulram.assetaccess.domain.AssetClassification;
import com.mithulram.assetaccess.domain.AssetEntity;

import java.time.Instant;
import java.util.UUID;

public record AssetResponse(UUID id, String name, String ownerTeam, AssetClassification classification, Instant createdAt) {
    public static AssetResponse from(AssetEntity asset) {
        return new AssetResponse(asset.getId(), asset.getName(), asset.getOwnerTeam(), asset.getClassification(), asset.getCreatedAt());
    }
}
