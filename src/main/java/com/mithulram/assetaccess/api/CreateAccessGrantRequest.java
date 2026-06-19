package com.mithulram.assetaccess.api;

import com.mithulram.assetaccess.domain.AssetPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccessGrantRequest(
        @NotBlank @Size(max = 120) String principal,
        @NotNull AssetPermission permission) {
}
