package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.DeliveryRequest;
import org.zerock.b01.domain.Material;
import org.zerock.b01.service.AllSearch;

import java.util.List;
import java.util.Optional;

public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, String>, AllSearch {

    @Query("select d from DeliveryRequest d where d.drCode=:drCode")
    Optional<DeliveryRequest> findByDeliveryRequestCode(@Param("drCode") String drCode);

    @Query("SELECT DISTINCT dr.material.mName FROM DeliveryRequest dr")
    List<String> findDistinctMaterialNames();


    @Query("SELECT DISTINCT dr.drState FROM DeliveryRequest dr")
    List<CurrentStatus> findDistinctDrStates();
}
