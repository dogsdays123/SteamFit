package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, String>, AllSearch {

    @Query("select pp from ProductionPlan pp where pp.ppCode=:ppCode")
    ProductionPlan findByProductionPlanCodeObj(String ppCode);

    @Query("select pp from ProductionPlan pp where pp.ppCode=:ppCode")
    Optional<ProductionPlan> findByProductionPlanCode(String ppCode);

    @Query("select pp from ProductionPlan pp where pp.ppCode=:ppCode")
    ProductionPlan findByProductionPerDay(String ppCode);

    @Query("select p from Product p where p.pName=:pName")
    Product findByProduct(String pName);

    @Query("select pp from ProductionPlan pp")
    List<ProductionPlan> findByPlans();

    @Query("select dpp.dppCode from DeliveryProcurementPlan dpp where dpp.productionPlan=:ppObj")
    List<String> findDppCodeByPpCode(ProductionPlan ppObj);

    @Query("select pp from ProductionPlan pp where pp.ppCode=:ppCode")
    void deleteAllById(List<String> ppCodes);

    boolean existsByProduct_pCode(String pCode);

    @Query(
            value = "SELECT DATE_FORMAT(reg_date, '%Y-%m') AS month, p_name, SUM(pp_num) AS total, " +
                    "LAG(SUM(pp_num)) OVER (PARTITION BY p_name ORDER BY DATE_FORMAT(reg_date, '%Y-%m')) AS previous_total " +
                    "FROM production_plan " +
                    "GROUP BY month, p_name " +
                    "ORDER BY month",
            nativeQuery = true
    )
    List<Object[]> findMonthlyProductPlanCountsByProduct();
}
