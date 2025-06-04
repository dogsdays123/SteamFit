package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.UserBy;

public interface CountBy extends JpaRepository<UserBy, String> {

    @Query("SELECT MAX(p.pCode) FROM Product p WHERE p.pCode LIKE CONCAT(:prefix, '%')")
    String findMaxPCode(String prefix);

    @Query("SELECT MAX(pp.ppCode) FROM ProductionPlan pp WHERE pp.ppCode LIKE CONCAT(:prefix, '%')")
    String findMaxPpCode(String prefix);

    @Query("SELECT MAX(m.mCode) FROM Material m WHERE m.mCode LIKE CONCAT(:prefix, '%')")
    String findMaxMCode(String prefix);

    @Query("SELECT MAX(dpp.dppCode) FROM DeliveryProcurementPlan dpp WHERE dpp.dppCode LIKE CONCAT(:prefix, '%')")
    String findMaxDppCode(String prefix);

    @Query("SELECT MAX(ob.oCode) FROM OrderBy ob WHERE ob.oCode LIKE CONCAT(:prefix, '%')")
    String findMaxOCode(String prefix);

    @Query("SELECT MAX(dr.drCode) FROM DeliveryRequest dr WHERE dr.drCode LIKE CONCAT(:prefix, '%')")
    String findMaxDrCode(String prefix);
}
