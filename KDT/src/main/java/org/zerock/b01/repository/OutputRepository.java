package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.InPut;
import org.zerock.b01.domain.OutPut;
import org.zerock.b01.service.AllSearch;
import org.zerock.b01.service.OutputService;

import java.util.Optional;

public interface OutputRepository extends JpaRepository<OutPut, String>, AllSearch {

    Optional<OutPut> findTopByOrderByOpCodeDesc();
}
