package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.DeliveryRequest;
import org.zerock.b01.domain.InPut;
import org.zerock.b01.service.AllSearch;

import java.util.Optional;

public interface InputRepository extends JpaRepository<InPut, String>, AllSearch {

    Optional<InPut> findTopByOrderByIpCodeDesc();
}
