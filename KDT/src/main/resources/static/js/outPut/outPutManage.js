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

    const ppCode = firstRow[2].innerText;
    const mCode = firstRow[6].innerText;
    const mName = firstRow[7].innerText;
    const value = parseInt(firstRow[9].innerText);
    const isAvailable = isNaN(value) ? 0 : value;

    if (isAvailable === 0) {
        alert('해당 항목은 출고 가능한 수량이 없습니다.');
        return;
    }
    const regDateInput = document.getElementById("regDate");
    const currentDate = new Date().toISOString().split('T')[0];

    document.getElementById('ppCodeHidden').value = ppCode;
    document.getElementById('mCode').innerText = mCode;
    document.getElementById('mCodeHidden').value = mCode;
    document.getElementById('mName').innerText = mName;
    document.getElementById('isAvailable').innerText = isAvailable;
    regDateInput.value = currentDate;

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});

document.getElementById('opANum').addEventListener('input', function () {
    const opANumInput = this;
    const isAvailable = parseInt(document.getElementById('isAvailable').innerText);
    const opANum = parseInt(opANumInput.value);

    if (isNaN(opANum)) {
        alert("출고 수량을 입력하세요.");
        opANumInput.value = '';
        opANumInput.focus();
        return;
    }

    if (opANum <= 0) {
        alert("출고 수량은 0보다 커야 합니다.");
        opANumInput.value = '';
        opANumInput.focus();
        return;
    }

    if (opANum > isAvailable) {
        alert("출고 수량은 출고 가능 수량을 초과할 수 없습니다.");
        opANumInput.value = '';
        opANumInput.focus();
        return;
    }
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/outPut/outPutManage'
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