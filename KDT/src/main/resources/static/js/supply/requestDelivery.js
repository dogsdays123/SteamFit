document.querySelectorAll('.selectPlan').forEach((checkbox) => {
    checkbox.addEventListener('change', function () {
        if (this.checked) {
            document.querySelectorAll('.selectPlan').forEach((cb) => {
                if (cb !== this) cb.checked = false;
            });
        }
    });
});

document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const firstRow = selectedRows[0].children;

    const planCodeInput = firstRow[1].innerText;
    const sId = firstRow[2].innerText;
    const productSupplier = firstRow[3].innerText;
    const mCode = firstRow[4].innerText;
    const productNameInput = firstRow[5].innerText;
    const availableQuantity = firstRow[6].innerText;
    const currentState = firstRow[9].innerText.trim();

    if (currentState === '납품 요청') {
        alert('선택한 항목은 이미 납품 요청이 완료되었습니다.');
        return;
    }


    if (currentState !== '준비 완료') {
        alert('선택한 항목은 아직 납품 준비가 완료되지 않았습니다.');
        return;
    }

    document.getElementById('requestQuantity').addEventListener('input', function () {
        const availableQuantity = parseInt(firstRow[6].innerText);
        const requestQuantity = parseInt(this.value);

        if (requestQuantity > availableQuantity) {
            alert('납품 요청 수량은 납품 가능 수량을 초과할 수 없습니다.');
            this.value = ''; // 잘못 입력한 값 비우기
            this.focus();
        }
    });

    document.getElementById('planCodeInput').innerText = planCodeInput;
    document.getElementById('oCodeHidden').value = planCodeInput;
    document.getElementById('sIdHidden').value = sId;
    document.getElementById('mCodeHidden').value = mCode;
    document.getElementById('productSupplier').innerText = productSupplier;
    document.getElementById('productNameInput').innerText = productNameInput;
    document.getElementById('availableQuantity').value = availableQuantity;

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/supply/requestDelivery'
}, false)

document.querySelector(".pagination").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    const target = e.target

    if (target.tagName !== 'A') {
        return
    }

    const num = target.getAttribute("data-num")

    const formObj = document.querySelector("form")

    formObj.innerHTML += `<input type='hidden' name='page' value='${num}'>`

    formObj.submit()
}, false)

document.addEventListener("DOMContentLoaded", () => {
    const states = {
        ON_HOLD: ["대기", "bg-secondary"],
        HOLD_PROGRESS: ["검수 대기", "bg-light text-dark"],
        HOLD_DELIVERY: ["납품 대기", "bg-warning text-dark"],
        APPROVAL: ["승인", "bg-primary"],
        IN_PROGRESS: ["진행 중", "bg-info text-dark"],
        UNDER_INSPECTION: ["검수 중", "bg-warning"],
        RETURNED: ["반품", "bg-danger"],
        FINISHED: ["종료", "bg-dark"],
        REJECT: ["거절", "bg-danger"],
        DELIVERED: ["배달 완료", "bg-success"],

        ARRIVED: ["도착", "bg-indigo"],
        NOT_REMAINING: ["재고 없음", "bg-danger"],

        SUCCESS_INSPECTION: ["검수 완료", "bg-teal"],
        READY_SUCCESS: ["준비 완료", "bg-orange"],
        DELIVERY_REQUESTED: ["납품 요청됨", "bg-yellow text-dark"],
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
    };



    // 테이블 내 표시용
    document.querySelectorAll('[data-state]').forEach(function (td) {
        const state = td.dataset.state;
        const [label, badgeClass] = stateMap[state] || ["알 수 없음", "bg-light text-dark"];
        td.innerHTML = `<span class="badge ${badgeClass}">${label}</span>`;
    });

    // 드롭다운 option에 텍스트 설정
    document.querySelectorAll(".oState option").forEach(option => {
        const state = option.dataset.state;
        if (!state) {
            option.textContent = "전체";
        } else {
            option.textContent = states[state] || "알 수 없음";
        }
    });
});