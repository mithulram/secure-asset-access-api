package com.mithulram.assetaccess.api;

import com.mithulram.assetaccess.domain.AccessGrantEntity;
import com.mithulram.assetaccess.domain.AssetPermission;

import java.time.Instant;
import java.util.UUID;

public record AccessGrantResponse(UUID id, UUID assetId, String principal, AssetPermission permission, Instant grantedAt) {
    public static AccessGrantResponse from(AccessGrantEntity grant) {
        return new AccessGrantResponse(grant.getId(), grant.getAssetId(), grant.getPrincipal(), grant.getPermission(), grant.getGrantedAt());
    }
}
