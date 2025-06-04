package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.service.StateService;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class StateServiceImpl implements StateService {

    @Override
    public String StateCheck(CurrentStatus currentStatus) {
        switch (currentStatus) {
            case ON_HOLD:
                return "대기중";
            case APPROVAL:
                return  "승인";
            case IN_PROGRESS:
                return "진행 중";
            case UNDER_INSPECTION:
                return "검수 중";
            case RETURNED:
                return "반품";
            case FINISHED:
                return "종료";
            case REJECT:
                return "거절";
            case DELIVERED:
                return "배달 완료";
            case ARRIVED:
                return "도착";
            case NOT_REMAINING:
                return "재고없음";
            default:
                return "해당없음";
        }
    }
}
