const stateMap = {
    ON_HOLD: ["대기", "bg-secondary"],
    HOLD_PROGRESS: ["검수 대기", "bg-secondary"],
    HOLD_DELIVERY: ["납품 대기", "bg-warning text-dark"],
    APPROVAL: ["승인", "bg-primary"],
    IN_PROGRESS: ["진행 중", "bg-info text-dark"],
    UNDER_INSPECTION: ["검수 중", "bg-warning"],
    RETURNED: ["반품", "bg-danger"],
    RETURNED_REQUESTED: ["반품 요청", "bg-danger"],
    FINISHED: ["종료", "bg-dark"],
    REJECT: ["거절", "bg-danger"],
    DELIVERED: ["배달 완료", "bg-success"],

    ARRIVED: ["도착", "bg-indigo"],
    NOT_REMAINING: ["재고 없음", "bg-danger"],

    SUCCESS_INSPECTION: ["검수 완료", "bg-teal"],
    READY_SUCCESS: ["준비 완료", "bg-orange"],
    DELIVERY_REQUESTED: ["납품 요청", "bg-yellow text-dark"],
    DELIVERY_DELIVERED: ["납품 완료", "bg-green"],
    SUCCESS: ["전체 완료", "bg-success"],
    DPP_SUCCESS: ["등록 완료", "bg-success"],

    DPP: ["조달", "bg-purple"],
    ORDER_BY: ["발주", "bg-pink"],
    DELIVERY_REQUEST: ["납품", "bg-blue"],
    INPUT: ["입고", "bg-cyan"],
    INPUT_SUCCESS: ["입고 완료", "bg-success"],
    OUTPUT: ["출고", "bg-orange"],
    OUTPUT_SUCCESS: ["출고 완료", "bg-orange"]
}

document.querySelectorAll(".CheckState").forEach(td => {
    const state = td.dataset.state;
    const [label, badgeClass] = stateMap[state] || ["알 수 없음", "bg-light text-dark"];
    td.innerHTML = `<span class="badge ${badgeClass}">${label}</span>`;
});