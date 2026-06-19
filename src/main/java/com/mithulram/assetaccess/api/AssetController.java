package com.mithulram.assetaccess.api;

import com.mithulram.assetaccess.domain.AccessGrantEntity;
import com.mithulram.assetaccess.domain.AssetEntity;
import com.mithulram.assetaccess.service.AssetAccessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final AssetAccessService service;

    public AssetController(AssetAccessService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetResponse createAsset(@Valid @RequestBody CreateAssetRequest request, Authentication authentication) {
        return AssetResponse.from(service.createAsset(request, authentication.getName()));
    }

    @GetMapping
    public List<AssetResponse> listAssets() {
        return service.listAssets().stream().map(AssetResponse::from).toList();
    }

    @GetMapping("/{assetId}")
    public AssetResponse getAsset(@PathVariable UUID assetId) {
        return AssetResponse.from(service.getAsset(assetId));
    }

    @PostMapping("/{assetId}/access-grants")
    @ResponseStatus(HttpStatus.CREATED)
    public AccessGrantResponse grantAccess(
            @PathVariable UUID assetId,
            @Valid @RequestBody CreateAccessGrantRequest request,
            Authentication authentication) {
        AccessGrantEntity grant = service.grantAccess(assetId, request, authentication.getName());
        return AccessGrantResponse.from(grant);
    }

    @GetMapping("/{assetId}/access-grants")
    public List<AccessGrantResponse> listAccessGrants(@PathVariable UUID assetId) {
        return service.listAccessGrants(assetId).stream().map(AccessGrantResponse::from).toList();
    }
}
