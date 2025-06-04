package org.zerock.b01.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.*;

import java.time.LocalDate;

public interface AllSearch {
    Page<UserByAllDTO> userBySearchWithAll(String[] types, String keyword, String uName,
                                           String userJob, String userRank, LocalDate regDate,
                                           String status, String uId, Pageable pageable);
    }
