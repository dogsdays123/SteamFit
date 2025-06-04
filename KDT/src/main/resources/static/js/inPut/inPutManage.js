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
        .map(cb => cb.closest('tr'));

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const firstRow = selectedRows[0].children;

    const drCode = firstRow[1].innerText;
    const oCode = firstRow[2].innerText;
    const mName = firstRow[4].innerText;
    const sName = firstRow[5].innerText;
    const drNum = parseInt(firstRow[6].textContent.trim()) || 0;

    const regDateInput = document.getElementById("regDate");
    const currentDate = new Date().toISOString().split('T')[0];

    const currentState = firstRow[8].innerText.trim();

    if (currentState !== '납품 완료') {
        alert('선택한 항목은 아직 납품 준비가 완료되지 않았습니다.');
        return;
    }

    console.log("납품 수량 (drNum):", drNum);
    document.getElementById('drCode').innerText = drCode;
    document.getElementById('hiddenDrCode').value = drCode;
    document.getElementById('hiddenOCode').value = oCode;
    document.getElementById('mName').innerText = mName;
    document.getElementById('hiddenMName').value = mName;
    document.getElementById('sName').innerText = sName;
    document.getElementById('hiddenSName').value = sName;
    document.getElementById('drNum').innerText = drNum;
    document.getElementById('hiddenDrNum').value = drNum;
    regDateInput.value = currentDate;

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();

    const ipNumInput = document.getElementById("ipNum");
    const ipTrueNumInput = document.getElementById("ipTrueNum");
    const ipFalseNumInput = document.getElementById("ipFalseNum");

    function validateInputs() {
        const ipNum = parseInt(ipNumInput.value) || 0;
        const ipTrueNum = parseInt(ipTrueNumInput.value) || 0;
        const ipFalseNum = parseInt(ipFalseNumInput.value) || 0;

        if (ipNum > drNum) {
            alert("입고 수량은 납품 수량을 초과할 수 없습니다.");
            ipNumInput.value = '';
            return false;
        }


        if (ipTrueNum + ipFalseNum > ipNum) {
            alert("합격 수량과 불량 수량의 합은 입고 수량을 초과할 수 없습니다.");
            ipTrueNumInput.value = '';
            ipFalseNumInput.value = '';
            return false;
        }

        return true;
    }

    ipNumInput.addEventListener("input", validateInputs);
    ipTrueNumInput.addEventListener("input", validateInputs);
    ipFalseNumInput.addEventListener("input", validateInputs);

    ipTrueNumInput.addEventListener("blur", () => {
        const ipNum = parseInt(ipNumInput.value) || 0;
        const ipTrueNum = parseInt(ipTrueNumInput.value) || 0;

        if (ipTrueNum > ipNum) {
            alert("합격 수량은 입고 수량보다 많을 수 없습니다.");
            ipTrueNumInput.value = '';
            ipFalseNumInput.value = '';
            return;
        }

        ipFalseNumInput.value = ipNum - ipTrueNum;
    });
});

document.addEventListener("DOMContentLoaded", function () {
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
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/inPut/inPutManage'
}, false)

// document.querySelector(".pagination").addEventListener("click", function (e) {
//     e.preventDefault()
//     e.stopPropagation()
//
//     const target = e.target
//
//     if (target.tagName !== 'A') {
//         return
//     }
//
//     const num = target.getAttribute("data-num")
//
//     const formObj = document.querySelector("form")
//
//     formObj.innerHTML += `<input type='hidden' name='page' value='${num}'>`
//
//     formObj.submit()
// }, false)
