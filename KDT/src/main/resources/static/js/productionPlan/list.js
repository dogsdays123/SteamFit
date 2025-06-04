document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('ppEnd').addEventListener('change', function () {
    const ppStart = new Date(document.getElementById('ppStart').value);
    const ppEnd = new Date(this.value);

    if (ppEnd < ppStart) {
        alert("종료일은 시작일보다 이전일 수 없습니다.");
        this.value = '';
    }
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr');
        const cells = row.children;
        const currentState = cells[7].innerText.trim();
        console.log(currentState);

        if (currentState == '등록 완료') {
            alert('선택한 항목은 이미 조달 계획이 등록되어 수정이 불가능합니다.');
            return;
        }
        // 각 td 값을 가져오기
        const ppCode = row.querySelector('td:nth-child(2)').innerText;
        const pCode = row.querySelector('td:nth-child(3)').innerText;
        const pName = row.querySelector('td:nth-child(3)').innerText;
        const pStart = row.querySelector('td:nth-child(4)').innerText;
        const pEnd = row.querySelector('td:nth-child(5)').innerText;
        const pNum = row.querySelector('td:nth-child(6)').innerText;




        document.getElementById('planCodeInput').value = ppCode;
        document.getElementById('ppProductName').value = pName;
        document.getElementById('ppStartDay').value = pStart;
        document.getElementById('ppEndDay').value = pEnd;
        document.getElementById('ppNum').value = pNum;

        // 모달 띄우기
        const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
        modal.show();
    });
});

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    const ppCodes = [];

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const ppCode = cells[1].textContent.trim();
        ppCodes.push(ppCode);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="ppCodes"]').forEach(input => input.remove());

    ppCodes.forEach(ppCode => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'ppCodes';
        hiddenInput.value = ppCode;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});
