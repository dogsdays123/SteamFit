let errorChecks = null;
let duplicates = null;
let selectedFiles = []; // 전역 변수로 따로 관리
let rowIndex = 0;

function addPlan() {
    const pName = document.getElementById('pName').value;
    const mComponentType = document.getElementById('mComponentType').value;
    const mType = document.getElementById('mType').value;
    const mName = document.getElementById('mName').value;
    const depth = document.getElementById('depth').value;
    const height = document.getElementById('height').value;
    const width = document.getElementById('width').value;
    const weight = document.getElementById('weight').value;
    const unitPrice = document.getElementById('unitPrice').value;
    const mMinNum = document.getElementById('mMinNum').value;
    const uId = document.getElementById("uId").value;
    //uId는 따로 받아온다.

    if (!pName || !mType || !mName
        || !depth || !height || !width || !weight || !unitPrice || !mMinNum) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="materials[${rowIndex}].pName" value="${pName}">${pName}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mComponentType" value="${mComponentType}">${mComponentType}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mType" value="${mType}">${mType}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mName" value="${mName}">${mName}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mMinNum" value="${mMinNum}">${mMinNum}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mDepth" value="${depth}">${depth}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mHeight" value="${height}">${height}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mWidth" value="${width}">${width}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mWeight" value="${weight}">${weight}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mUnitPrice" value="${unitPrice}">${unitPrice}</td>
        <td><input type="hidden" name="materials[${rowIndex}].uId" value="${uId}">${uId}</td> 
        <td>
          <button type="button" class="icon-button" onclick="removeRow(this)" aria-label="삭제" title="해당 행 삭제">
            <i class="bi bi-x-lg"></i>
          </button>
        </td>
    `;

    tableBody.appendChild(newRow);
    rowIndex++;

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

    // 입력값 초기화
    document.getElementById('pName').value = '';
    document.getElementById('mComponentType').value = '';
    document.getElementById('mType').value = '';
    document.getElementById('mName').value = '';
    document.getElementById('depth').value = '';
    document.getElementById('height').value = '';
    document.getElementById('width').value = '';
    document.getElementById('weight').value = '';
    document.getElementById('unitPrice').value = '';
    document.getElementById('mMinNum').value = '';
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
    $.ajax({
        url: '/material/addMaterial',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            errorChecks = response.errorCheck;
            duplicates = response.duplicate;
        },
        error: function (xhr, status, error) {
            alert("파일 확인에 실패. : " + error);
        }
    });
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
    reader.onload = function(e) {
        const data = e.target.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 });

        const tableHeader = document.getElementById('tableHeader');
        const tableBody = document.getElementById('tableBody');

        tableHeader.innerHTML = '';
        tableBody.innerHTML = '';

        rows[0]?.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            tableHeader.appendChild(th);
        });

        rows.slice(1).forEach(row => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-file-name', file.name);

            row.forEach((cell, colIndex) => {
                const td = document.createElement('td');
                td.textContent = cell;

                if (colIndex === 0 && errorChecks.includes(cell)) {
                    td.style.color = 'red';
                    td.style.fontWeight = 'bold';
                } else if (colIndex === 3) {
                    const currentPName = row[0]; // 상품명은 0번째 열
                    const currentMName = cell;   // 3번째 열: 부품명
                    const isDuplicate = duplicates?.some(d =>
                        d.pName === currentPName && d.mName === currentMName
                    );

                    if (isDuplicate) {
                        td.style.color = 'red';
                        td.style.fontWeight = 'bold';
                    }
                }

                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });

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
        url: '/material/addMaterial',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function() {
            $('#loadingModal').modal('show');  // 로딩 모달 띄우기
        },
        success: function (response) {
            errorChecks = response.errorCheck;
            duplicates = response.duplicate;

            document.getElementById('fileList').innerHTML = '';
            document.getElementById('uploadedFileList').style.display = 'none';
            document.getElementById('fileTable').style.display = 'none';
            $('#excelFile').val('');
            document.getElementById('fileListContainer').style.display = 'none';

            if (confirm("목록 페이지로 이동하시겠습니까?")) {
                window.location.href = "/material/materialList";
            } else {
                window.location.href = "/material/materialRegister";
            }
            setTimeout(() => {
                $('#makeAdminModal').modal('hide');
            }, 500); // 0.5초 후에 닫기

        },
        error: function (xhr, status, error) {
            alert("파일 업로드에 실패했습니다. : " + error);
            window.location.href = "/material/materialRegister";
        }
    });
});


$(document).ready(function () {
// 상품 선택 시 동적으로 부품 목록 업데이트
    $('#pName').on('change', function () {
        const pName = $(this).val();  // 선택된 상품 값

        // 상품 선택 시 부품 목록을 업데이트
        console.log('선택된 상품:', pName);

        if (pName) {
            // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
            const pNameEncode = encodeURIComponent(pName);

            // AJAX를 사용하여 부품 목록을 가져오는 코드
            $.ajax({
                url: `/material/${pNameEncode}/cType`,  // URL 인코딩 적용
                method: 'GET',  // HTTP GET 요청
                success: function(componentTypes) {
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
                error: function(error) {
                    console.error('부품 목록을 가져오는 중 오류 발생:', error);
                }
            });
        } else {
            // 상품이 선택되지 않은 경우
            $('#mComponentType').empty();  // 부품 목록 초기화
            $('#mComponentType').append('<option value="" selected>선택</option>');
            $('#mComponentType').trigger('change');
        }
    });
});