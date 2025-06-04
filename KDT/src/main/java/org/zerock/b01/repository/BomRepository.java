package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface BomRepository extends JpaRepository<Bom, Long>, AllSearch {

    @Query("select p from Product p where p.pName=:pName")
    Product findByProductByPName(String pName);

    @Query("select b from Bom b where b.product.pName=:pName and b.material.mName=:mName")
    Bom findByOthers(@Param("pName") String pName, @Param("mName") String mName);

    @Query("select m from Material m where m.mCode=:mCode")
    Optional<Material> findByMaterialCode(@Param("mCode") String mCode);

    @Query("SELECT m FROM Material m WHERE m.product.pCode = :pCode")
    List<Product> findByProductCode(@Param("pCode") String pCode);

    @Query("SELECT DISTINCT b.bRequireNum FROM Bom b WHERE b.material.mCode = :mCode")
    String findRequireNumByMaterialCode(@Param("mCode") String mCode);


}
