package com.mithulram.assetaccess.api;

import com.mithulram.assetaccess.domain.AssetClassification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAssetRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String ownerTeam,
        @NotNull AssetClassification classification) {
}
