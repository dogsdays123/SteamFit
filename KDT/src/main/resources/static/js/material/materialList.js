let worldPName;

document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

        // 각 td 값을 가져오기
        const pName = row.querySelector('td:nth-child(2)').innerText;
        const CType = row.querySelector('td:nth-child(3)').innerText;
        const mCode = row.querySelector('td:nth-child(4)').innerText;
        const mType = row.querySelector('td:nth-child(5)').innerText;
        const mName = row.querySelector('td:nth-child(6)').innerText;
        const mMinNum = row.querySelector('td:nth-child(7)').innerText;
        const mDepth = row.querySelector('td:nth-child(9)').innerText;
        const mHeight = row.querySelector('td:nth-child(10)').innerText;
        const mWidth = row.querySelector('td:nth-child(11)').innerText;
        const mWeight = row.querySelector('td:nth-child(12)').innerText;
        const mUnitPrice = row.querySelector('td:nth-child(13)').innerText;


        document.getElementById('ppProductName').value = pName;
        document.getElementById('ppComponentType').value = CType;
        document.getElementById('ppMCode').value = mCode;
        document.getElementById('ppMType').value = mType;
        document.getElementById('ppMName').value = mName;
        document.getElementById('ppMMinNum').value = mMinNum;
        document.getElementById('ppMDepth').value = mDepth;
        document.getElementById('ppMHeight').value = mHeight;
        document.getElementById('ppMWidth').value = mWidth;
        document.getElementById('ppMWeight').value = mWeight;
        document.getElementById('ppMUnitPrice').value = mUnitPrice;

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


    const pCodes = [];

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const pCode = cells[3].textContent.trim();
        pCodes.push(pCode);


        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[11].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="pCodes"]').forEach(input => input.remove());

    pCodes.forEach(pCode => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'pCodes';
        hiddenInput.value = pCode;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/supply/materialList'
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

$(document).ready(function () {

    $('#pName').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        worldPName = input;
        loadMType(input);
    });

    $('#mComponentType').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        loadMName(input);
    });

});

function loadMType(pName) {

    if (pName) {
        // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
        const pNameEncode = encodeURIComponent(pName);

        // AJAX를 사용하여 부품 목록을 가져오는 코드
        $.ajax({
            url: `/material/${pNameEncode}/cType`,  // URL 인코딩 적용
            method: 'GET',  // HTTP GET 요청
            success: function (componentTypes) {
                // 부품명 선택 요소 초기화
                const componentSelect = $('#mComponentType');
                componentSelect.empty();  // 기존 옵션 제거

                // "선택" 옵션 추가
                componentSelect.append('<option value="" selected>선택</option>');

                // 받아온 부품 목록을 옵션으로 추가
                componentTypes.forEach(type => {
                    componentSelect.append(`<option value="${type}">${type}</option>`);
                });

                // select2 업데이트
                componentSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
            },
            error: function (error) {
                console.error('부품 목록을 가져오는 중 오류 발생:', error);
            }
        });
    } else {
        $('#mComponentType').empty();  // 부품 목록 초기화
        var mComponentListHTML = $('#mComponentListHTML').html();  // 서버에서 렌더링된 HTML 가져오기
        $('#mComponentType').append(mComponentListHTML);  // mNameList의 option을 append
        $('#mComponentType').trigger('change');  // 변경 이벤트 트리거
    }
}

function loadMName(mType) {

    if (mType) {
        // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
        const mTypeEncode = encodeURIComponent(mType);
        const pNameEncode = encodeURIComponent(worldPName);

        // AJAX를 사용하여 부품 목록을 가져오는 코드
        $.ajax({
            url: `/material/${mTypeEncode}/${pNameEncode}/mName`,  // URL 인코딩 적용
            method: 'GET',  // HTTP GET 요청
            success: function (mNames) {
                // 부품명 선택 요소 초기화
                const mNameSelect = $('#mName');
                mNameSelect.empty();  // 기존 옵션 제거

                // "선택" 옵션 추가
                mNameSelect.append('<option value="" selected>선택</option>');

                // 받아온 부품 목록을 옵션으로 추가
                mNames.forEach(type => {
                    mNameSelect.append(`<option value="${type}">${type}</option>`);
                });

                // select2 업데이트
                mNameSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
            },
            error: function (error) {
                console.error('부품 목록을 가져오는 중 오류 발생:', error);
            }
        });
    } else {
        $('#mName').empty();  // 부품 목록 초기화
        var mComponentListHTML = $('#mNameListHTML').html();  // 서버에서 렌더링된 HTML 가져오기
        $('#mName').append(mComponentListHTML);  // mNameList의 option을 append
        $('#mName').trigger('change');  // 변경 이벤트 트리거
    }
}
