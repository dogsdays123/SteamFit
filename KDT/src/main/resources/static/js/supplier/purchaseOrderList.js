document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll(".totalPrice").forEach(el => {
    const value = parseInt(el.textContent, 10);
    el.textContent = value.toLocaleString(); // → "1,271,600"
});

document.querySelectorAll(".unitPrice").forEach(el => {
    const value = parseInt(el.textContent, 10);
    el.textContent = value.toLocaleString(); // → "1,271,600"
});

// document.getElementById('openPurchaseModal').addEventListener('click', function () {
//     const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
//         .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기
//
//     if (selectedRows.length === 0) {
//         alert('하나 이상의 항목을 선택해주세요.');
//         return;
//     }
//
//     if (selectedRows.length > 1) {
//         alert('거래명세서 발급은 1개 항목만 선택 가능합니다.');
//         return;
//     }
//
//     const tbody = document.getElementById('purchaseOrderModalBody');
//     tbody.innerHTML = ''; // 기존 내용 비우기
//
//     selectedRows.forEach(checkbox => {
//         const row = checkbox.closest('tr');
//         const cells = row.querySelectorAll('td');
//         const newRow = document.createElement('tr');
//
//         newRow.innerHTML = `
//             <td>${cells[1].textContent.trim()}</td>
//             <td>${cells[2].textContent.trim()}</td>
//             <td>${cells[3].textContent.trim()}</td>
//             <td>${cells[4].textContent.trim()}</td>
//             <td>${cells[5].textContent.trim()}</td>
//             <td>${cells[6].textContent.trim()}</td>
//         `;
//
//         tbody.appendChild(newRow);
//     });
//
//     const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
//     modal.show();
// });

function collectSelectedPlans() {
    const selected = document.querySelectorAll('.selectPlan:checked');
    const plans = [];

    selected.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        console.log("추출된 셀 수:", cells.length);
        cells.forEach((c, idx) => console.log(`셀 ${idx}:`, c.textContent.trim()));
        plans.push({
            materialName: cells[3].textContent.trim(),
            quantity: cells[4].textContent.trim(),
            unitPrice: cells[5].textContent.trim(),
            dueDate: cells[7].textContent.trim(),
            width: cells[9].textContent.trim(), // 가로
            depth: cells[10].textContent.trim(), // 깊이
            height: cells[11].textContent.trim() // 높이세로 규격 가로x깊이x높이
        });
    });

    return plans;
}

function previewPurchaseOrderPDF() {
    const selectedPlans = collectSelectedPlans();
    if (selectedPlans.length === 0) {
        alert('조달계획을 최소 1개 이상 선택해주세요.');
        return;
    }

    // const formData = collectFormData();
    const formData = {
        plans: selectedPlans
    };
    // formData.items = selectedPlans; // 👉 선택된 품목 리스트 추가

    fetch('/supplier/purchase/order/pdf/preview', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
        .then(res => res.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            window.open(url, '_blank'); // 새 창으로 PDF 미리보기
        })
        .catch(() => alert('미리보기에 실패했습니다.'));
}



function generatePurchaseOrderPDF(){
    const selectedPlans = collectSelectedPlans();
    if (selectedPlans.length === 0) {
        alert('조달계획을 최소 1개 이상 선택해주세요.');
        return;
    }
    const formData = {
        plans: selectedPlans
    };

    fetch('/supplier/purchase/order/pdf/download', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
        .then(res => res.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `발주서_${formData.planCode}.pdf`;
            a.click();
            alert('발주서가 PDF로 생성되어 메일로 전송되었습니다.');
            const modal = bootstrap.Modal.getInstance(document.getElementById('purchaseOrderModal'));
            if (modal) modal.hide();
        })
        .catch(() => alert('발주서 생성에 실패했습니다.'));
}

// document.getElementById('openPurchaseDelModal').addEventListener('click', function () {
//
//     const selectedRows = document.querySelectorAll('.selectPlan:checked');
//     if (selectedRows.length === 0) {
//         alert('삭제할 항목을 선택해 주세요.');
//         return;
//     }
//
//     const tbody = document.getElementById('deleteTableBody');
//     tbody.innerHTML = ''; // 기존 내용 비우기
//
//     selectedRows.forEach(checkbox => {
//         const row = checkbox.closest('tr');
//         const cells = row.querySelectorAll('td');
//         const newRow = document.createElement('tr');
//
//         newRow.innerHTML = `
//             <td>${cells[1].textContent.trim()}</td>
//             <td>${cells[2].textContent.trim()}</td>
//             <td>${cells[3].textContent.trim()}</td>
//             <td>${cells[4].textContent.trim()}</td>
//             <td>${cells[5].textContent.trim()}</td>
//             <td>${cells[6].textContent.trim()}</td>
//         `;
//
//         tbody.appendChild(newRow);
//     });
//
//     const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
//     modal.show();
// });

document.getElementById('openPurchaseReadyModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    const oCodes = new Set();

    const tbody = document.getElementById('readyTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const oCode = cells[1].textContent.trim();
        oCodes.add(oCode);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderReadyModalForm');
    form.querySelectorAll('input[name="oCodes"]').forEach(input => input.remove());
    console.log(oCodes);
    oCodes.forEach(oCode => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'oCodes';
        hiddenInput.value = oCode;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalReady'));
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

    self.location = '/supplier/purchaseOrderList'
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