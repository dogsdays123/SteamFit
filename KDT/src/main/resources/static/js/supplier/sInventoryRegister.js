let mCodeChecks = null;
let errorChecks = null;
let selectedFiles = []; // 전역 변수로 따로 관리
let pNameWorld;

function addPlan() {
    const productName = document.getElementById('productName').value;
    const componentType = document.getElementById('componentType').value;
    const materialList = document.getElementById('materialList');
    const ssNum = document.getElementById('ssNum').value;
    const ssMinOrderQty = document.getElementById('ssMinOrderQty').value;
    // const unitPrice = document.getElementById('unitPrice').value;
    const leadTime = document.getElementById('leadTime').value;

    const materialName = materialList.options[materialList.selectedIndex].text;


    if (!productName || !componentType || !componentType || !materialList
        || !ssNum || !ssMinOrderQty || !leadTime) {

        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="pNames[]" value="${productName}">${productName}</td> 
        <td><input type="hidden" name="cTypes[]" value="${componentType}">${componentType}</td> 
        <td><input type="hidden" name="mNames[]" value="${materialName}">${materialName}</td> 
        <td><input type="hidden" name="ssNums[]" value="${ssNum}">${ssNum}</td> 
        <td><input type="hidden" name="ssMinOrderQty[]" value="${ssMinOrderQty}">${ssMinOrderQty}</td> 
        <td><input type="hidden" name="leadTimes[]" value="${leadTime}">${leadTime}</td> 
        <td>
          <button type="button" class="icon-button" onclick="removeRow(this)" aria-label="삭제" title="해당 행 삭제">
            <i class="bi bi-x-lg"></i>
          </button>
        </td>
    `;

    tableBody.appendChild(newRow);


    const planRows = tableBody.querySelectorAll('tr:not(#registerRow)');
    if (planRows.length === 0) {
        const existingRegisterRow = document.getElementById('registerRow');
        if (existingRegisterRow) {
            existingRegisterRow.remove();
        }
        return;
    }

    if (!document.getElementById('registerRow')) {
        const registerRow = document.createElement('tr');
        registerRow.id = 'registerRow';
        registerRow.innerHTML = `
        <td colspan="15" class="text-center" style="padding: 10px">
            <div class="d-flex justify-content-center gap-2">
                <button type="button" class="btn btn-outline-dark btn-sm" onclick="clearPlanTable()">전체 삭제</button>
                <button type="submit" class="btn btn-dark btn-sm">전체 등록</button>
            </div>
        </td>
    `;
        tableBody.appendChild(registerRow);
    } else {
        // 항상 버튼을 마지막으로 이동
        const registerRow = document.getElementById('registerRow');
        tableBody.appendChild(registerRow);
    }


    // 입력값 초기화
    document.getElementById('productName').selectedIndex = 0;
    document.getElementById('productCode').value = '';
    document.getElementById('componentType').selectedIndex = 0;
    document.getElementById('materialList').selectedIndex = 0;
    document.getElementById('materialCode').value = '';
    document.getElementById('ssNum').value = '';
    document.getElementById('ssMinOrderQty').value = '';
    document.getElementById('leadTime').value = '';
}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(button) {
    const row = button.closest('tr');
    row.remove();

    // #registerRow를 제외한 나머지 데이터 행이 0개인지 확인
    const tableBody = document.getElementById('materialTableBody'); // 실제 tbody id로 변경
    const remainingRows = tableBody.querySelectorAll('tr:not(#registerRow)');

    if (remainingRows.length === 0) {
        const registerRow = document.getElementById('registerRow');
        if (registerRow) {
            registerRow.remove();
        }
    }
}

function clearPlanTable() {
    const tableBody = document.querySelector("#planTable tbody");
    tableBody.innerHTML = ""; // 모든 row 삭제
}

document.getElementById('excelFile').addEventListener('change', function (event) {
    const files = Array.from(event.target.files);
    selectedFiles = files;

    updateFileListUI();
});

function updateFileListUI() {
    const fileList = document.getElementById('fileListName');
    fileList.innerHTML = '';
    document.getElementById('fileListContainer').style.display = 'block';

    if (selectedFiles.length === 0) {
        document.getElementById('fileListContainer').style.display = 'none';
        document.getElementById('excelFile').value = '';
        return;
    }

    //----------------------업로드된 파일 확인용

    const formData = new FormData();
    const uId = $('#uId').val();

    selectedFiles.forEach(file => {
        formData.append('file', file);
    });

    formData.append('uId', uId);
    formData.append('check', "true");

    // AJAX 요청 보내기
    // $.ajax({
    //     url: '/supplier/addSStock',
    //     method: 'POST',
    //     data: formData,
    //     processData: false,
    //     contentType: false,
    //     success: function (response) {
    //         mCodeChecks = response.mCodes;
    //         errorChecks = response.errorCheck;
    //         console.log(mCodeChecks);
    //         console.log(errorChecks);
    //     },
    //     error: function (xhr, status, error) {
    //         alert("파일 확인에 실패. : " + error);
    //     }
    // });
    //--------------------------------------

    selectedFiles.forEach((file, index) => {
        const li = document.createElement('li');
        li.className = 'list-group-item d-flex justify-content-between align-items-center';
        li.setAttribute('data-index', index);

        const nameSpan = document.createElement('span');
        nameSpan.className = 'file-name';
        nameSpan.textContent = file.name;
        nameSpan.style.cursor = 'pointer';

        nameSpan.addEventListener('click', () => {
            loadFileContent(file, index);
        });

        const deleteBtnWrap = document.createElement('div');
        deleteBtnWrap.classList.add('tooltip-wrap-mtop');

        const tooltipText = document.createElement('span');
        tooltipText.classList.add('tooltip-text-mtop');
        tooltipText.textContent = '해당 파일 삭제';

        const deleteIcon = document.createElement('i');
        deleteIcon.className = 'bi bi-x-lg deleteIcon text-danger';
        deleteIcon.style.cursor = 'pointer';

        deleteBtnWrap.appendChild(deleteIcon);
        deleteBtnWrap.appendChild(tooltipText);

        deleteIcon.addEventListener('click', () => {
            selectedFiles.splice(index, 1);
            updateFileListUI();

            const currentTableFile = document.getElementById('fileTable').getAttribute('data-file-name');
            if (currentTableFile === file.name) {
                document.getElementById('fileTable').style.display = 'none';
            }
        });

        li.appendChild(nameSpan);
        li.appendChild(deleteBtnWrap);
        fileList.appendChild(li);
    });
}

function loadFileContent(file, index) {
    const reader = new FileReader();
    reader.onload = function (e) {
        const data = e.target.result;
        const workbook = XLSX.read(data, {type: 'binary'});
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const rows = XLSX.utils.sheet_to_json(sheet, {header: 1});

        const tableHeader = document.getElementById('tableHeader');
        const tableBody = document.getElementById('tableBody');

        tableHeader.innerHTML = '';
        tableBody.innerHTML = '';

        let mCodes = [];

        rows[0]?.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            tableHeader.appendChild(th);
        });

        rows.slice(1).forEach(row => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-file-name', file.name);

            const materialCode = row[0];

            mCodes.push(materialCode);

            row.forEach((cell, colIndex) => {
                const td = document.createElement('td');
                td.textContent = cell;

                if (colIndex === 6 && mCodeChecks.includes(cell)) {
                    td.style.color = 'red';
                    td.style.fontWeight = 'bold';
                }
                // row.forEach(cell => {
                //     const td = document.createElement('td');
                //     td.textContent = cell;
                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });

        console.log("mCodes: ", mCodes);

        const fileTable = document.getElementById('fileTable');
        fileTable.setAttribute('data-file-name', file.name);
        fileTable.style.display = 'block';
    };
    reader.readAsBinaryString(file);
}

$(document).on('click', '.deleteIcon', function () {
    const fileItem = $(this).closest('li');
    const fileName = fileItem.find('.file-name').text().trim();

    selectedFiles = selectedFiles.filter(file => file.name !== fileName);

    updateFileListUI();
});

$('#excelUpload').on('click', function (e) {
    e.preventDefault();

    if (selectedFiles.length === 0) {
        alert('업로드할 파일이 없습니다.');
        return;
    }

    const formData = new FormData();
    const uId = $('#uId').val();

    selectedFiles.forEach(file => {
        formData.append('file', file);
    });

    formData.append('uId', uId);
    formData.append('check', "false");

    // AJAX 요청 보내기
    $.ajax({
        url: '/supplier/addSStock',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            mCodeChecks = response.mCodes;
            errorChecks = response.errorCheck;
            console.log(mCodeChecks);
            console.log(errorChecks);
            if (response.isAvailable) {
                alert("파일 업로드에 성공했습니다.(특정)");
            } else {
                alert("파일 업로드에 성공했습니다.");
            }
            document.getElementById('fileList').innerHTML = '';
            document.getElementById('uploadedFileList').style.display = 'none';
            document.getElementById('fileTable').style.display = 'none';
            $('#excelFile').val('');
            document.getElementById('fileListContainer').style.display = 'none';

            if (confirm("목록 페이지로 이동하시겠습니까?")) {
                window.location.href = "/supplier/sInventoryList";
            } else {
                window.location.href = "/supplier/sInventoryRegister";
            }

        },
        error: function (xhr, status, error) {
            alert("중복된 정보를 제외한 새로운 정보 등록이 완료되었습니다.: " + error);
            window.location.href = "/supplier/sInventoryRegister";
        }
    });
});

$(document).ready(function () {
    $('#productName').on('change', function () {
        const input = $(this).val();
        pNameWorld = input;
        if (!input) return;
        else {
            loadMType(input);
        }
    });

    $('#componentType').on('change', function () {
        const input = $(this).val();
        if (!input) return;
        else {
            loadMName(input, pNameWorld);
        }
    });
})

function loadMType(pName) {
    if (pName) {
        // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
        const pNameEncode = encodeURIComponent(pName);

        // AJAX를 사용하여 부품 목록을 가져오는 코드
        $.ajax({
            url: `/supplier/api/products/${pNameEncode}/component-types`,  // URL 인코딩 적용
            method: 'GET',  // HTTP GET 요청
            success: function (componentTypes) {
                // 부품명 선택 요소 초기화
                const componentSelect = $('#componentType');
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
    }
}

function loadMName(mComponentType, pName) {
    if (!pName || !mComponentType) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = [encodeURIComponent(mComponentType), encodeURIComponent(pName)];

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/supplier/api/products/${encodes[0]}/${encodes[1]}/mName`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (mNames) {
            if (!Array.isArray(mNames) || mNames.length === 0) {
                defaultValueInner("mName");
                return;  // 이 시점에서 종료해주는 게 좋음
            }
            // 부품명 선택 요소 초기화
            const mNameSelect = $('#materialList');
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

document.getElementById("materialList").addEventListener("change", function () {
    let selectedMaterial = this.options[this.selectedIndex];
    let materialCode = selectedMaterial.getAttribute("data-code");  // 자재 코드
    let materialName = selectedMaterial.getAttribute("data-name");  // 자재 이름

    // 자재 코드 입력 필드에 자재 코드 자동 입력
    document.getElementById("materialCode").value = materialCode;
    document.getElementById("materialList").options[this.selectedIndex].text = materialName;

});