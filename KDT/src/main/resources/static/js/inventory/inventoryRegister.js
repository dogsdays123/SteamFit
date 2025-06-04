let pNameWorld;

function addPlan() {
    const productName = document.getElementById('productName').value;
    const componentType = document.getElementById('componentType').value;
    const materialList = document.getElementById('materialList');
    const isNum = document.getElementById('isNum').value;
    const isLocation = document.getElementById('isLocation').value;

    const materialName = materialList.options[materialList.selectedIndex].text;


    if (!productName || !componentType || !materialList || !isNum
        || !isLocation) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="pNames[]" value="${productName}">${productName}</td> 
        <td><input type="hidden" name="cTypes[]" value="${componentType}">${componentType}</td> 
        <td><input type="hidden" name="mNames[]" value="${materialName}">${materialName}</td> 
        <td><input type="hidden" name="isNums[]" value="${isNum}">${isNum}</td> 
        <td><input type="hidden" name="isLoca[]" value="${isLocation}">${isLocation}</td> 
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
        return; // 항목 없으면 함수 종료
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
    ``

    // 입력값 초기화
    document.getElementById('productName').selectedIndex = 0;
    document.getElementById('productCode').value = '';
    document.getElementById('componentType').selectedIndex = 0;
    document.getElementById('materialList').selectedIndex = 0;
    document.getElementById('materialCode').value = '';
    document.getElementById('isNum').value = '';
    document.getElementById('isLocation').value = '';


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

$(document).ready(function () {
    $('#pName').select2({
        placeholder: "검색 또는 직접 입력",
        tags: true, // ← 이게 핵심! 직접 입력 허용
        allowClear: true,
        createTag: function (params) {
            const term = $.trim(params.term);
            if (term === '') {
                return null;
            }
            return {
                id: term,
                text: term,
                newTag: true // 사용자 입력값 구분
            };
        },
        theme: 'bootstrap-5' // 부트스트랩 5 테마 적용
    });
});

$(document).ready(function () {
    $('#productName').on('change', function () {
        const input = $(this).val();
        pNameWorld = input;
        if (!input) return;
        else{loadMType(input);}
    });

    $('#componentType').on('change', function () {
        const input = $(this).val();
        if (!input) return;
        else{loadMName(input, pNameWorld);}
    });
})

function loadMType(pName){
    if (!pName) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(pName);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/inventory/api/products/${encodes}/component-types`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (componentTypes) {
            console.log(componentTypes);
            if (!Array.isArray(componentTypes) || componentTypes.length === 0) {
                return;  // 이 시점에서 종료해주는 게 좋음
            }
            // 부품명 선택 요소 초기화
            const ComponentTypeSelect = $('#componentType');
            ComponentTypeSelect.empty();  // 기존 옵션 제거
            ComponentTypeSelect.append('<option value="" selected>선택</option>');
            componentTypes.forEach(type => {
                ComponentTypeSelect.append(`<option value="${type}">${type}</option>`);
            });
            ComponentTypeSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}

function loadMName(mComponentType, pName) {
    if (!pName || !mComponentType) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = [encodeURIComponent(mComponentType), encodeURIComponent(pName)];

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/inventory/api/products/${encodes[0]}/${encodes[1]}/mName`,  // URL 인코딩 적용
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


document.getElementById("materialList").addEventListener("change", function() {
    let selectedMaterial = this.options[this.selectedIndex];
    let materialCode = selectedMaterial.getAttribute("data-code");  // 자재 코드
    let materialName = selectedMaterial.getAttribute("data-name");  // 자재 이름

    // 자재 코드 입력 필드에 자재 코드 자동 입력
    document.getElementById("materialCode").value = materialCode;
    document.getElementById("materialList").options[this.selectedIndex].text = materialName;

});

let selectedFiles = []; // 전역 변수로 따로 관리


document.getElementById('excelFile').addEventListener('change', function(event) {
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
    reader.onload = function(e) {
        const data = e.target.result;
        const workbook = XLSX.read(data, { type: 'binary', cellDates: true }); // cellDates: true 추가
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 });

        // 날짜 변환: rows 에 직접 적용
        rows.forEach((row, rowIndex) => {
            row.forEach((cell, colIndex) => {
                if (colIndex === 3 || colIndex === 4) { // 3, 4번 컬럼
                    if (cell instanceof Date) {
                        row[colIndex] = cell.toISOString().split('T')[0]; // yyyy-mm-dd 형태로 변환
                    }
                }
            });
        });

        const tableHeader = document.getElementById('tableHeader');
        const tableBody = document.getElementById('tableBody');

        tableHeader.innerHTML = '';
        tableBody.innerHTML = '';

        let pCodes = [];
        let pNames = [];

        rows[0]?.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            tableHeader.appendChild(th);
        });

        rows.slice(1).forEach(row => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-file-name', file.name);

            const productCode = row[0];
            const productName = row[1];

            pCodes.push(productCode);
            pNames.push(productName);

            row.forEach(cell => {
                const td = document.createElement('td');
                td.textContent = cell;
                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });

        console.log("pCodes: ", pCodes);
        console.log("pNames: ", pNames);

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
    const whereValue = $('input[name="where"]').val();

    selectedFiles.forEach(file => {
        formData.append('file', file);
    });

    formData.append('uId', uId);
    formData.append('where', whereValue);
    formData.append('whereToGo', 'register');

    // AJAX 요청 보내기
    $.ajax({
        url: '/inventory/addInventory',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
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
                window.location.href = "/inventory/inventoryList";
            } else {
                window.location.href = "/inventory/inventoryRegister";
            }

        },
        error: function(xhr, status, error) {
            alert("파일 업로드에 실패했습니다. : " + error);
        }
    });
});
