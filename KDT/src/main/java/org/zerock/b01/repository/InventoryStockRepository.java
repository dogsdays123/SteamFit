package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.InventoryStock;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long>, AllSearch {

    @Query("select i from InventoryStock i where i.material.mCode = :mCode")
    List<InventoryStock> findByMaterialCode(@Param("mCode") String mCode);

    boolean existsByMaterial_mCode(String mCode);

    @Query("SELECT DISTINCT is.isAvailable FROM InventoryStock is WHERE is.material.mCode = :mCode")
    String findAvailableNumByMaterialCode(@Param("mCode") String mCode);

    Optional<InventoryStock> findByMaterial_mCode(String materialCode);
}
