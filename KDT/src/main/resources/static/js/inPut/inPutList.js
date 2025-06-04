document.addEventListener("DOMContentLoaded", function () {
    const falseNumCells = document.querySelectorAll(".false-num");

    falseNumCells.forEach(cell => {
        const value = parseInt(cell.textContent.trim());
        if (!isNaN(value) && value > 0) {
            cell.style.color = "red";
            cell.style.fontWeight = "bold";
        }
    });
});

document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('반품 요청할 항목을 선택해주세요.');
        return;
    }

    if (selectedRows.length > 1) {
        alert('반품 요청은 하나의 항목만 선택이 가능합니다.');
        return;
    }
    const firstRow = selectedRows[0].children;
    const currentState = firstRow[10].innerText.trim();
    const drNum = parseInt(firstRow[8].textContent.trim()) || 0;

    if (currentState == '반품 요청') {
        alert('선택한 항목은 이미 반품 요청이 완료되었습니다.');
        return;
    }

    if (drNum === 0) {
        alert('해당 항목은 반품 대상이 아닙니다. 불량 수량을 확인해 주세요.');
        return;
    }

    const tbody = document.getElementById('returnTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
            <td style="color: red;">${cells[8].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);

        const form = document.getElementById('purchaseOrderForm');

        const ipCode = cells[1].textContent.trim(); // 입고 코드
        const rNum = cells[8].textContent.trim();   // 불량 수량

        const inputIpCode = document.createElement('input');
        inputIpCode.type = 'hidden';
        inputIpCode.name = 'ipCode';
        inputIpCode.value = ipCode;

        form.appendChild(inputIpCode);

        const inputRNum = document.createElement('input');
        inputRNum.type = 'hidden';
        inputRNum.name = 'rNum';
        inputRNum.value = rNum;

        form.appendChild(inputRNum);
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

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    const ipIds = [];

    let hasPending = false;

    for (let checkbox of selectedRows) {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const status = cells[10].textContent.trim();

        if (status === '진행 중') {
            hasPending = true;
            break;
        }
    }

    if (hasPending) {
        const proceed = confirm('선택한 항목 중 입고 처리가 완료되지 않은 항목이 포함되어 있습니다. 그래도 진행하시겠습니까?');
        if (!proceed) {
            return;
        }
    }


    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const ipId = cells[1].textContent.trim();
        ipIds.push(ipId);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
            <td>${cells[8].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="ipIds"]').forEach(input => input.remove());

    ipIds.forEach(ipId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'ipIds';
        hiddenInput.value = ipId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/inPut/inPutList'
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
        RETURNED_REQUESTED: ["반품 요청", "bg-danger"],
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

    document.querySelectorAll(".ipState option").forEach(option => {
        const state = option.dataset.state;
        if (!state) {
            option.textContent = "전체";
        } else {
            option.textContent = stateMap[state] || "알 수 없음";
        }
    });

    document.querySelectorAll('[data-state]').forEach(function (td) {
        const state = td.dataset.state;
        const [label, badgeClass] = stateMap[state] || ["알 수 없음", "bg-light text-dark"];
        td.innerHTML = `<span class="badge ${badgeClass}">${label}</span>`;
    });
});