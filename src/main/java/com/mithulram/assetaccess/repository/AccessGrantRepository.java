package com.mithulram.assetaccess.repository;

import com.mithulram.assetaccess.domain.AccessGrantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccessGrantRepository extends JpaRepository<AccessGrantEntity, UUID> {
    List<AccessGrantEntity> findByAssetIdOrderByGrantedAtDesc(UUID assetId);
}
