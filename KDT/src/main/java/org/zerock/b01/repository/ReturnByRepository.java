package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.InPut;
import org.zerock.b01.domain.ReturnBy;
import org.zerock.b01.service.AllSearch;

public interface ReturnByRepository extends JpaRepository<ReturnBy, Long>, AllSearch {
}
