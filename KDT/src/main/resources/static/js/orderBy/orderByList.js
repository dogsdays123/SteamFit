document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

        // 각 td 값을 가져오기
        const ppCode = row.querySelector('td:nth-child(2)').innerText;
        const pCode = row.querySelector('td:nth-child(3)').innerText;
        const pName = row.querySelector('td:nth-child(4)').innerText;
        const pStart = row.querySelector('td:nth-child(5)').innerText;
        const pEnd = row.querySelector('td:nth-child(6)').innerText;

        document.getElementById('planCodeInput').value = ppCode;
        document.getElementById('ppProductCode').value = pCode;
        document.getElementById('ppProductName').value = pName;
        document.getElementById('ppStartDay').value = pStart;
        document.getElementById('ppEndDay').value = pEnd;

        // 모달 띄우기
        const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
        modal.show();
    });
});

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
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
        `;

        tbody.appendChild(newRow);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});
