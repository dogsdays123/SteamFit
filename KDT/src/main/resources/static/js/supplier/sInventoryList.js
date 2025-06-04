document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/supplier/sInventoryList'
}, false)

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    const ssIds = [];

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const ssId = cells[1].textContent.trim();
        ssIds.push(ssId);

        newRow.innerHTML = `
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
    form.querySelectorAll('input[name="ssIds"]').forEach(input => input.remove());

    ssIds.forEach(ssId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'ssIds';
        hiddenInput.value = ssId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

        const ssId = row.querySelector('td:nth-child(2)').innerText;
        const mCode = row.querySelector('td:nth-child(3)').innerText;
        const mName = row.querySelector('td:nth-child(4)').innerText;
        const ssNum = row.querySelector('td:nth-child(5)').innerText;
        const ssMinOrderQty = row.querySelector('td:nth-child(6)').innerText;
        // const unitPrice = row.querySelector('td:nth-child(7)').innerText;
        const leadTime = row.querySelector('td:nth-child(7)').innerText;


        document.getElementById('ssId').value = ssId;
        document.getElementById('mCode').value = mCode;
        document.getElementById('mName').value = mName;
        document.getElementById('ssNum').value = ssNum;
        document.getElementById('ssMinOrderQty').value = ssMinOrderQty;
        // document.getElementById('unitPrice').value = unitPrice;
        document.getElementById('leadTime').value = leadTime;

        // 모달 띄우기
        const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
        modal.show();
    });
});

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