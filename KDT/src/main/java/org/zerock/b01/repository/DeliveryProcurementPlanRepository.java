package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.DeliveryProcurementPlan;
import org.zerock.b01.domain.Material;
import org.zerock.b01.service.AllSearch;

import java.util.List;

public interface DeliveryProcurementPlanRepository extends JpaRepository<DeliveryProcurementPlan, String>, AllSearch {

    @Query("select dpp.dppCode from DeliveryProcurementPlan dpp where dpp.productionPlan.ppCode =:ppCode")
    List<String> findDppCodeByPpCode(@Param("ppCode") String ppCode);

    @Query("select dpp.material.mName from DeliveryProcurementPlan dpp where dpp.dppCode =:dppCode")
    List<String> findMNameByDppCode(@Param("dppCode") String dppCode);

    @Query("select dpp.material.mName from DeliveryProcurementPlan dpp where dpp.dppCode =:dppCode")
    String findMNameByDppCodeOne(@Param("dppCode") String dppCode);

    @Query("SELECT DISTINCT dpp.dppState FROM DeliveryProcurementPlan dpp")
    List<CurrentStatus> findDppState();
}
