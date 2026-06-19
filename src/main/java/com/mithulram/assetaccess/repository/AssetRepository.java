package com.mithulram.assetaccess.repository;

import com.mithulram.assetaccess.domain.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssetRepository extends JpaRepository<AssetEntity, UUID> {
}
