let dppList = [];
let ppCode;
let mCodeForLeadTime;
let pNameWorld;

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('dppForm');
    const buttons = document.querySelectorAll('.openDppModal');

    buttons.forEach(function (button) {
        button.addEventListener('click', function () {

            // 클릭된 버튼의 부모 <tr>을 찾음
            const row = button.closest('tr');
            const ppCode1 = row.children[0].textContent.trim();
            const pName = row.children[1].textContent.trim();
            const ppNum = row.children[4].textContent.trim();

            ppCode = ppCode1;

            // 모달에 값 주입
            document.getElementById('planCodeInput').textContent = ppCode1;
            document.getElementById('productNameInput').textContent = pName;
            document.getElementById('productQtyInput').textContent = ppNum;

            // 모달 열기
            const modal = new bootstrap.Modal(document.getElementById('procurementModal'));
            modal.show();
        });
    });

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // 기본 제출 막기
        const mCode = document.getElementById('mCode').value;
        const planCode = document.getElementById('planCodeInput').textContent;
        const releaseQty = $('#procuredQty').val();
        const uId = 'someUserId';

        // submitDppForm 함수 호출
        submitDppForm(mCode, planCode, releaseQty, uId);

        alert(`조달 계획이 등록되었습니다`);

        // 폼을 서버에 전송하는 로직
        form.submit(); // 수동으로 다시 제출

        const modal = bootstrap.Modal.getInstance(document.getElementById('orderByModal'));
        if (modal) modal.hide();

        // if (tbody) tbody.innerHTML = '';
        // orderByList.length = 0;
    });

    $('#requiredQty').on('input', function () {
        $('#procuredQty').trigger('input'); // 다시 한 번 조달수량 체크
    });

    $(document).ready(function () {
        $('#procuredQty').on('input', function () {
            const required = parseInt($('#requireNumInput').val(), 10);
            const procured = parseInt($(this).val(), 10);

            if (!isNaN(required) && !isNaN(procured) && procured > required) {
                alert('조달수량은 요구수량을 초과할 수 없습니다.');
                $(this).val(required); // 조달수량을 요구수량으로 되돌림
            }
        });
    });
});

function addProcurement(button) {
    const container = button.closest('.tab-pane'); // 현재 탭 안

    const textInputs = container.querySelectorAll('input[type="text"]');
    const numberInputs = container.querySelectorAll('input[type="number"]');
    const selectElements = container.querySelectorAll('select');
    const dateInput = container.querySelector('input[type="date"]');

// 공급업체, 자재명, 자재코드
    const mName = selectElements.length > 1 ? selectElements[1].value : '';
    const mCode = selectElements.length > 2 ? selectElements[2].value : '';
    const supplier = selectElements.length > 3 ? selectElements[3].value : '';

// 수량
    const needQty = numberInputs.length > 0 ? numberInputs[0].value.trim() : '';
    const supplyQty = numberInputs.length > 1 ? numberInputs[1].value.trim() : '';
    const outputQty = numberInputs.length > 2 ? numberInputs[2].value.trim() : '';
// 납기일
    const dueDate = dateInput ? dateInput.value.trim() : '';

    // 유효성 체크 (선택)
    if (!supplier || !mName || !mCode || !needQty || !supplyQty || !dueDate) {
        alert('필수 정보를 입력해주세요');
        return;
    }

    // 데이터 객체 생성
    const item = {
        supplier,
        mName,
        mCode,
        needQty,
        supplyQty,
        outputQty,
        ppCode,
        dueDate
    };

    console.log(item);

    dppList.push(item);

    renderProcurementTable();
}


function renderProcurementTable() {
    const uId = document.getElementById('uId').value;
    const tbody = document.getElementById('dppListBody');
    tbody.innerHTML = ''; // 초기화
    let rowIndex = 0;

    dppList.forEach((item, index) => {
        const row = document.createElement('tr');
        //이런식으로 input값 넣어줄거임
        row.innerHTML = `
      <td><input type="hidden" name="dpps[${rowIndex}].mName" value="${item.mName}">${item.mName}</td>
      <td><input type="hidden" name="dpps[${rowIndex}].mCode" value="${item.mCode}">${item.mCode}</td>
      <td><input type="hidden" name="dpps[${rowIndex}].sName" value="${item.supplier}">${item.supplier}</td>
      <td class="text-center"><input type="hidden" name="dpps[${rowIndex}].dppRequireNum" value="${item.needQty}">${item.needQty}</td>
      <td class="text-center"><input type="hidden" name="dpps[${rowIndex}].dppNum" value="${item.supplyQty}">${item.supplyQty}</td>
      <td class="text-center"><input type="hidden" name="dpps[${rowIndex}].outputQty" value="${item.outputQty}">${item.outputQty}</td>  
      <td class="text-center"><input type="hidden" name="dpps[${rowIndex}].dppDate" value="${item.dueDate}">${item.dueDate}</td>
      <td class="text-center">
              <input type="hidden" name="dpps[${rowIndex}].ppCode" value="${item.ppCode}">
                <button type="button" class="icon-button"  onclick="removeProcurement(${index})" aria-label="삭제" title="해당 행 삭제">
            <i class="bi bi-x-lg"></i>
          </button>


      </td>
    `;
        tbody.appendChild(row);
        rowIndex++;
    });
}

function removeProcurement(index) {
    dppList.splice(index, 1);
    renderProcurementTable();
}

function resetModalFields() {
    // select 초기화
    $('#mComponentType').val('').trigger('change');
    $('#mName').empty().append('<option value="" selected>선택</option>').trigger('change');
    $('#mCode').empty().append('<option value="" selected>선택</option>').trigger('change');
    $('#sup').empty().append('<option value="" selected>선택</option>').trigger('change');

    // input 초기화
    $('#needQty').val('');
    $('#supplyQty').val('');
    $('#dueDate').val('');
    $('#leadTime').val('');

    // 내부 테이블 초기화
    const tbody = document.getElementById('dppListBody');
    if (tbody) tbody.innerHTML = '';
    dppList.length = 0;
}

$(document).ready(function () {

    $('#procurementModal').on('show.bs.modal', function () {
        resetModalFields();
        pNameWorld = document.getElementById('productNameInput').textContent;  // 선택된 상품 값
        loadMType(pNameWorld);
    });

    $('#mComponentType').on('change', function () {
        const input = $(this).val();
        if (!input) defaultValue("mName");
        else{
            loadMName(input, pNameWorld);
        }
    });

    $('#mName').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        if (!input) defaultValue("mCode");
        else {
            loadMCode(input);
        }
    });

    $('#mCode').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        if (!input) defaultValue("sup");
        else{
            mCodeForLeadTime = input;
            loadSup(input);
        }
    });

    $('#sup').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        if (!input) defaultValue("leadTime");
        else {
            loadLeadTime(input, mCodeForLeadTime);
            loadRequireNum(mCodeForLeadTime);
            loadProcureNum(mCodeForLeadTime);
        }
    });
});

function defaultValue(id){
    const mComponentTypeSelect = $(`#${id}`);
    mComponentTypeSelect.empty();
    mComponentTypeSelect.append('<option value="" selected>값을 선택</option>');
    mComponentTypeSelect.trigger('change');
}

function defaultValueInner(id){
    const mComponentTypeSelect = $(`#${id}`);
    mComponentTypeSelect.empty();
    mComponentTypeSelect.append('<option value="" selected>등록이 필요합니다.</option>');
    mComponentTypeSelect.trigger('change');
}

function loadMType(pName) {
    if (!pName) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(pName);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes}/mComponentType`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (mComponentTypes) {
            if (!Array.isArray(mComponentTypes) || mComponentTypes.length === 0) {
                defaultValueInner("mComponentType");
                return;  // 이 시점에서 종료해주는 게 좋음
            }
            // 부품명 선택 요소 초기화
            const mComponentTypeSelect = $('#mComponentType');
            mComponentTypeSelect.empty();  // 기존 옵션 제거
            mComponentTypeSelect.append('<option value="" selected>선택</option>');
            mComponentTypes.forEach(type => {
                mComponentTypeSelect.append(`<option value="${type}">${type}</option>`);
            });
            mComponentTypeSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}

function loadMName(mComponentType, pName) {
    if (!pName) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = [encodeURIComponent(mComponentType), encodeURIComponent(pName)];

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes[0]}/${encodes[1]}/mName`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (mNames) {
            if (!Array.isArray(mNames) || mNames.length === 0) {
                defaultValueInner("mName");
                return;  // 이 시점에서 종료해주는 게 좋음
            }
            // 부품명 선택 요소 초기화
            const mNameSelect = $('#mName');
            mNameSelect.empty();  // 기존 옵션 제거
            mNameSelect.append('<option value="" selected>선택</option>');
            mNames.forEach(type => {
                mNameSelect.append(`<option value="${type}">${type}</option>`);
            });
            mNameSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}

function loadMCode(mName) {
    if (!mName) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(mName);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes}/mCode`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (mCodes) {
            if (!Array.isArray(mCodes) || mCodes.length === 0) {
                defaultValueInner("mCode");
                return;  // 이 시점에서 종료해주는 게 좋음
            }
            // 부품명 선택 요소 초기화
            const mCodeSelect = $('#mCode');
            mCodeSelect.empty();  // 기존 옵션 제거
            mCodeSelect.append('<option value="" selected>선택</option>');
            mCodes.forEach(type => {
                mCodeSelect.append(`<option value="${type}">${type}</option>`);
            });
            mCodeSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}

function loadSup(mCode) {
    if (!mCode) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(mCode);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes}/ss`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (sss) {
            if (!Array.isArray(sss) || sss.length === 0) {
                defaultValueInner("sup");
                return;  // 이 시점에서 종료해주는 게 좋음
            }
            // 부품명 선택 요소 초기화
            const sssSelect = $('#sup');
            sssSelect.empty();  // 기존 옵션 제거
            sssSelect.append('<option value="" selected>선택</option>');
            sss.forEach(type => {
                sssSelect.append(`<option value="${type}">${type}</option>`);
            });
            sssSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}

function loadLeadTime(sup, mCode) {
    if (!sup) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = [];
    encodes[0] = encodeURIComponent(sup);
    encodes[1] = encodeURIComponent(mCode);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes[0]}/${encodes[1]}/leadTime`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (leadTime) {
            // 리드타임 입력 필드에 값 설정
            const leadTimeInput = $('#leadTime');
            if (leadTime) {
                leadTimeInput.val(leadTime);
            } else {
                leadTimeInput.val('미배정');
            }
        },
        error: function (error) {
            console.error('리드타임 가져오다가 오류 발생:', error);
        }
    });
}

function loadRequireNum(mCode) {
    const encodes = [];
    encodes[1] = encodeURIComponent(mCode);

    console.log(mCode, encodes[1]);

    $.ajax({
        url: `/dpp/${encodes[1]}/rn`,
        method: 'GET',
        success: function (data) {
            const leadTimeInput = $('#requireNumInput');
            const productQtyInput = $('#productQtyInput');


            const productQty = parseFloat(productQtyInput.text()) || 0;
            const leadTime = parseFloat(data) || 0;  // 서버에서 받은 값도 숫자로 처리
            if (data) {
                leadTimeInput.val(productQty * leadTime);
                loadProcureNum(mCode);
            } else {
                leadTimeInput.val('미배정');
            }
        },
        error: function () {
            $('#requireNum').text('에러');
        }
    });
}

let availableQty = 0;

function loadProcureNum(mCode) {
    const encodes = [];
    encodes[1] = encodeURIComponent(mCode);

    $.ajax({
        url: `/dpp/${encodes[1]}/an`, // 가용 수량 API
        method: 'GET',
        success: function (data) {
            const procuredQtyInput = $('#procuredQty');          // 조달 수량 input
            const requireNumInput = $('#requireNumInput');       // 총 필요 수량 input
            const outPutQty = $('#outPutQty');

            const requiredQty = parseFloat(requireNumInput.val()) || 0;
            availableQty = parseFloat(data) || 0;

            const procurementQty = Math.max(requiredQty - availableQty, 0);
            outPutQty.val(availableQty);
            procuredQtyInput.val(procurementQty);
        },
        error: function () {
            $('#procuredQty').val('에러');
        }
    });
}

function submitDppForm(mCode, planCode, releaseQty, uId) {
    const formData = {
        uId: uId,
        mCode: mCode,
        planCode: planCode,
        releaseQty: releaseQty,
        availableQty: availableQty// 자동으로 계산된 출고 수량
    };

    console.log("Sending formData to server: ", formData);

    $.ajax({
        url: '/dpp/outPutRegister',  // 출고 등록 API
        method: 'POST',
        data: formData,
        success: function(response) {
            // 출고 등록 성공 후 처리
            // alert('출고 등록이 완료되었습니다.');
        },
        error: function(xhr, status, error) {
            console.log("에러 발생:", error);
            console.log("xhr 상태:", xhr);
            alert('출고 등록 중 오류가 발생했습니다.');
        }
    });
}

function resetView() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('procurementModal'));

    const tbody = document.getElementById('dppListBOdy');
    if (tbody) tbody.innerHTML = '';
    dppList.length = 0;

    if (modal) modal.hide();
}

