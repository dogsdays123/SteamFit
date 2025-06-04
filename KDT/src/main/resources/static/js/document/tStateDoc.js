let obCodes = [];

document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('orderDocPDF').addEventListener('click', function () {
    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('PDF 발행할 발주서를 선택해주세요.');
        return;
    }

    const tbody = document.getElementById('pdfBody');
    tbody.innerHTML = '';

    obCodes = [];

    selectedRows.forEach((checkbox, index) => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const obCode = cells[1].textContent.trim();
        obCodes.push(obCode);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>   
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
            <td>${cells[8].textContent.trim()}</td>
            <td>${cells[9].textContent.trim()}</td>
            <td><button type="button" class="btn btn-outline-primary btn-sm" onclick="previewOrderPDF('${obCode}')">미리보기</button></td>
        `;

        tbody.appendChild(newRow);
    });

    const modal = new bootstrap.Modal(document.getElementById('pdfModal'));
    modal.show();
})


function generateOrderPDF() {

    const formData = {
        plans: obCodes
    };

    fetch('/document/pdf/s', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(formData)
    })
        .then(res => res.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `거래명세서_${formData.planCode}.pdf`;
            a.click();
            alert('거래명세서가 PDF로 생성되어 메일로 전송되었습니다.');
            const modal = bootstrap.Modal.getInstance(document.getElementById('purchaseOrderModal'));
            if (modal) modal.hide();
        })
        .catch(() => alert('거래명세서 생성에 실패했습니다.'));
}

function previewOrderPDF(obCode) {
    const formData = {
        plans: [obCode]
    };

    fetch('/document/pdf/s', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(formData)
    })
        .then(res => res.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            window.open(url, '_blank'); // 새 창으로 PDF 미리보기
        })
        .catch(() => alert('미리보기에 실패했습니다.'));
}

$(document).ready(function () {
    $('#obCode').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        loadMName(input);
    });

});

function loadMName(obCode) {
    if (!obCode) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(obCode);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/document/${encodes}/mNameList`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (mName) {
            console.log(mName);
            // 부품명 선택 요소 초기화
            const mNameSelect = $('#mName');
            mNameSelect.empty();  // 기존 옵션 제거
            mNameSelect.append('<option value="" selected>선택</option>');
            mNameSelect.append(`<option value="${mName}">${mName}</option>`);
            mNameSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}