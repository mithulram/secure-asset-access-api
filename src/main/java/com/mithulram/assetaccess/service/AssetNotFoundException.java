package com.mithulram.assetaccess.service;

import java.util.UUID;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(UUID assetId) {
        super("Asset not found: " + assetId);
    }
}
