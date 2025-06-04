package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, String>, AllSearch {
    @Query("select p from Product p where p.pName=:pName")
    Product findByProduct(String pName);

    @Query("select m from Material m where m.mCode=:mCode")
    Optional<Material> findByMaterialCode(@Param("mCode") String mCode);

    @Query("select m from Material m where m.mName=:mName")
    Optional<Material> findByMaterialName(@Param("mName") String mName);

    @Query("select m from Material m where m.mName=:mName")
    Optional<Material> findByMName(String mName);

    @Query("SELECT COUNT(m) FROM Material m WHERE m.mCode LIKE CONCAT(:prefix, '%')")
    Long countByPrefix(String prefix);

    @Query("SELECT m FROM Material m WHERE m.product.pCode = :pCode")
    List<Material> findByProductCode(@Param("pCode") String pCode);

    boolean existsByProduct_pCode(String pCode);

    @Query("select m from Material m where m.mName=:mName")
    Material findByName(@Param("mName") String mName);

    @Query("select m from Material m, Product p where p.pName=:pName and m.mName=:mName")
    Material findByOtherName(@Param("pName") String pName, @Param("mName") String mName);

    @Query("SELECT DISTINCT m.mComponentType FROM Material m WHERE m.product.pCode = :pCode")
    List<String> findComponentTypesByProductCode(@Param("pCode") String pCode);

    @Query("SELECT DISTINCT m.mComponentType FROM Material m WHERE m.product.pName = :pName")
    List<String> findComponentTypesByProductName(@Param("pName") String pName);

    @Query("select m.mName from Material m where m.product.pName = :pName")
    List<String> findMNameByPName(@Param("pName") String pName);

    @Query("select DISTINCT m.mComponentType from Material m where m.product.pName = :pName")
    List<String> findMComponentTypeByPName(@Param("pName") String pName);

    @Query("select m.mCode from Material m where m.mName = :mName")
    List<String> findMCodeByMNameList(@Param("mName") String mName);

    @Query("select m.mName from Material m where m.product.pName = :pName and m.mComponentType = :mComponentType")
    List<String> findMNameByETC(@Param("pName") String pName, @Param("mComponentType") String mComponentType);

    @Query("select m.mCode from Material m where m.mComponentType = :mType and m.mName = :mName")
    List<String> findMCodeByETC(@Param("mType") String mType, @Param("mName") String mName);

    @Query("SELECT m FROM Material m WHERE m.mComponentType = :componentType")
    List<Material> findByComponentType(@Param("componentType") String componentType);

    @Query("select m.mCode from Material m where m.mName = :mName")
    List<String> findMCodesByMName(@Param("mName") String mName);

    @Query("select m.mCode from Material m where m.mName = :mName")
    List<String> findMCodeByMName(@Param("mName") String mName);

}
