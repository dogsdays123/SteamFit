package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.InventoryStock;
import org.zerock.b01.domain.SupplierStock;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface SupplierStockRepository extends JpaRepository<SupplierStock, Long>, AllSearch {

    @Query("SELECT ss FROM SupplierStock ss WHERE ss.supplier.sId = :sId")
    List<SupplierStock> findAllBySupplierId(@Param("sId") Long sId);

    @Query("select ss from SupplierStock ss where ss.supplier.sId = :sId and ss.material.mCode=:mCode")
    SupplierStock findBySupplierId(@Param("sId") Long sId, @Param("mCode") String mCode);

    boolean existsBySupplier_sIdAndMaterial_mCode(Long sId, String mCode);

    @Query("SELECT ss.leadTime FROM SupplierStock ss WHERE ss.material.mCode =:mCode and ss.supplier.sId =:sId")
    String findLeadTimeByMCodeSName(@Param("mCode") String mCode, @Param("sId") Long sId);

    @Query("SELECT ss.supplier.sName FROM SupplierStock ss WHERE ss.material.mCode = :mCode")
    List<String> findSNameByMCode(@Param("mCode") String mCode);

    @Query("SELECT ss.leadTime FROM SupplierStock ss WHERE ss.supplier.sName=:sName and ss.material.mCode = :mCode")
    String findLeadTimeByETC(@Param("sName") String sName, @Param("mCode") String mCode);

    @Query("select sm.ssId from SupplierStock sm where sm.supplier.sId = :sid")
    List<Long> findSsidBySid(@Param("sid") String sid);

    @Query("SELECT DISTINCT s.material.mName FROM SupplierStock s WHERE s.supplier.sId = :sId")
    List<String> findDistinctMaterialNamesBySupplierId(@Param("sId") Long sId);

    @Query("SELECT DISTINCT s.material.mName FROM SupplierStock s WHERE s.material.mName IS NOT NULL")
    List<String> findAllDistinctMaterialNames();

    @Query("SELECT s FROM SupplierStock s WHERE s.material.mCode = :mCode AND s.supplier.sId = :sId")
    List<SupplierStock> findByMaterialMCodeAndSupplierSId(@Param("mCode") String mCode, @Param("sId") Long sId);
}

