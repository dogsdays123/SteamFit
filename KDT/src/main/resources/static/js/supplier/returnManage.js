document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('재납품할 항목을 선택해주세요.');
        return;
    }


    if (selectedRows.length > 1) {
        alert('재납품은 하나의 항목만 선택이 가능합니다.');
        return;
    }

    const firstRow = selectedRows[0].children;
    const currentState = firstRow[7].innerText.trim();

    if (currentState == '납품 완료') {
        alert('해당 항목은 재납품 대상이 아닙니다. 반품 상태를 확인해 주세요.');
        return;
    }

    const tbody = document.getElementById('redeliveryTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        newRow.innerHTML = `
            <td hidden="hidden">${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td style="color: blue;">${cells[5].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);

        const form = document.getElementById('purchaseOrderForm');

        const rId = cells[1].textContent.trim();
        const ipCode = cells[2].textContent.trim(); // 입고 코드
        const mCode = cells[3].textContent.trim();   // 불량 수량

        const inputRId = document.createElement('input');
        inputRId.type = 'hidden';
        inputRId.name = 'rId';
        inputRId.value = rId;

        form.appendChild(inputRId);

        const inputIpCode = document.createElement('input');
        inputIpCode.type = 'hidden';
        inputIpCode.name = 'ipCode';
        inputIpCode.value = ipCode;

        form.appendChild(inputIpCode);

        const inputMCode = document.createElement('input');
        inputMCode.type = 'hidden';
        inputMCode.name = 'mCode';
        inputMCode.value = mCode;

        form.appendChild(inputMCode);
    });


    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    if (selectedRows.length > 1) {
        alert('반품 정보 삭제는 하나의 항목만 선택이 가능합니다.');
        return;
    }

    const firstRow = selectedRows[0].closest('tr');
    const currentState = firstRow.children[7].innerText.trim();

    if (currentState == '대기') {
        alert('해당 항목은 삭제 대상이 아닙니다. 반품 상태를 확인해 주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    const rIds = [];

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const rId = cells[1].textContent.trim();
        rIds.push(rId);

        newRow.innerHTML = `
            <td hidden="hidden">${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="rIds"]').forEach(input => input.remove());

    rIds.forEach(rId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'rIds';
        hiddenInput.value = rId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});


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

    // 드롭다운 option에 텍스트 설정
    document.querySelectorAll(".drState option").forEach(option => {
        const state = option.dataset.state;
        if (!state) {
            option.textContent = "전체";
        } else {
            option.textContent = states[state] || "알 수 없음";
        }
    });

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

    self.location = '/supplier/returnManage'
}, false)