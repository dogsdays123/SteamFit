package org.zerock.b01.repository;

import org.hibernate.query.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.DeliveryRequest;
import org.zerock.b01.domain.OrderBy;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface OrderByRepository extends JpaRepository<OrderBy, String>, AllSearch {

    @Query("select o from OrderBy o where o.oCode=:oCode")
    Optional<OrderBy> findByOrderByCode(@Param("oCode") String oCode);

    @Query("select s.deliveryProcurementPlan.supplier.sName from OrderBy s")
    List<String> findSupplierNames();

    @Query("select distinct ob.deliveryProcurementPlan.material.mName from OrderBy ob")
    List<String> findMaterialNames();

    @Query("select distinct ob.deliveryProcurementPlan.supplier.sName from OrderBy ob")
    List<String> findSNames();

    @Query("SELECT FUNCTION('DATE_FORMAT', o.regDate, '%Y-%m') AS month, " +
            "SUM(CAST(o.oTotalPrice AS double)) " +
            "FROM OrderBy o GROUP BY month ORDER BY o.regDate ASC")
    List<Object[]> findMonthlyTotals();

    @Query("SELECT DISTINCT m.mName FROM OrderBy o JOIN o.deliveryProcurementPlan.material m GROUP BY m.mCode")
    List<String> findMaterialNamesDistinct();

    @Query("SELECT DISTINCT o.deliveryProcurementPlan.supplier.sName FROM OrderBy o")
    List<String> findSupplierNamesDistinct();

    @Query("SELECT DISTINCT o.oState FROM OrderBy o")
    List<CurrentStatus> findDistinctOrderStates();
}
