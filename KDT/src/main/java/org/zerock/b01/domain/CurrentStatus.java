package org.zerock.b01.domain;

public enum CurrentStatus {
    ON_HOLD, //대기
    HOLD_PROGRESS, //검수 대기
    HOLD_DELIVERY, //납품 대기
    APPROVAL, //승인
    IN_PROGRESS,    //진행 중
    UNDER_INSPECTION, //검수중
    RETURNED, // 반품, // 반품
    RETURNED_REQUESTED, // 반품 요청
    RETURNED_ALL, // 전체 반품
    PARTIAL_RETURN, // 부분 반품
    FINISHED, //종료
    REJECT, //거절
    DELIVERED, //배달 완료
    ARRIVED, //도착
    NOT_REMAINING, // 남은 게 없음
    SUCCESS_INSPECTION, //"검수 완료"
    SUCCESS, //"전체 완료"
    READY_SUCCESS, //"준비 완료"
    DELIVERY_REQUESTED, // 납품 요청
    DELIVERY_DELIVERED, // 납품 완료
    DPP_SUCCESS,  // 등록 완료
    DPP,  // 조달
    ORDER_BY,  // 발주
    DELIVERY_REQUEST, // 납품
    INPUT, // 입고
    INPUT_SUCCESS, // 입고 완료
    OUTPUT, // 출고
    OUTPUT_SUCCESS // 출고 완료
}
