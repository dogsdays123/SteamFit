document.getElementById('ppEnd').addEventListener('change', function () {
    const ppStart = new Date(document.getElementById('ppStart').value);
    const ppEnd = new Date(this.value);

    if (ppEnd < ppStart) {
        alert("종료일은 시작일보다 이전일 수 없습니다.");
        this.value = '';
    }
});

function addPlan() {
    const ppStart = document.getElementById('ppStart').value;
    const ppEnd = document.getElementById('ppEnd').value;
    const pName = document.getElementById('pName').value;
    const ppNum = document.getElementById('ppNum').value;
    const uId = document.getElementById("uId").value;



    let rowIndex = 0;

    if (!ppStart || !ppEnd || !pName || !ppNum) {
        alert('모든 항목을 입력해 주세요');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="plans[${rowIndex}].pName" value="${pName}">${pName}</td>
        <td><input type="hidden" name="plans[${rowIndex}].ppStart" value="${ppStart}">${ppStart}</td>
        <td><input type="hidden" name="plans[${rowIndex}].ppEnd" value="${ppEnd}">${ppEnd}</td>
        <td><input type="hidden" name="plans[${rowIndex}].ppNum" value="${ppNum}">${ppNum}</td>   
        <td><input type="hidden" name="plans[${rowIndex}].uId" value="${uId}">${uId}</td>       
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

// 등록/삭제 버튼 추가 또는 위치 재정렬
    if (!document.getElementById('registerRow')) {
        const registerRow = document.createElement('tr');
        registerRow.id = 'registerRow';
        registerRow.innerHTML = `
        <td colspan="11" class="text-center" style="padding: 10px">
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
    document.getElementById('ppStart').value = '';
    document.getElementById('ppEnd').value = '';
    document.getElementById('pName').value = '';
    document.getElementById('ppNum').value = '';
    document.getElementById('uId').value = '';

}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(button) {
    const row = button.closest('tr');
    row.remove();

    // #registerRow를 제외한 나머지 데이터 행이 0개인지 확인
    const tableBody = document.getElementById('ppTbody'); // 실제 tbody id로 변경
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
                if (colIndex === 1 || colIndex === 2) { // 3, 4번 컬럼
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

        let pNames = [];

        rows[0]?.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            tableHeader.appendChild(th);
        });

        rows.slice(1).forEach(row => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-file-name', file.name);

            const productName = row[0];

            pNames.push(productName);

            row.forEach(cell => {
                const td = document.createElement('td');
                td.textContent = cell;
                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });

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
        url: '/productionPlan/addProductPlan',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function() {
            $('#loadingModal').modal('show');  // 로딩 모달 띄우기
        },
        success: function(response) {
            alert("파일 업로드에 성공했습니다. " + response.mg);
            document.getElementById('fileList').innerHTML = '';
            document.getElementById('uploadedFileList').style.display = 'none';
            document.getElementById('fileTable').style.display = 'none';
            $('#excelFile').val('');
            document.getElementById('fileListContainer').style.display = 'none';

            if (confirm("목록 페이지로 이동하시겠습니까?")) {
                window.location.href = "/productionPlan/ppList";
            } else {
                window.location.href = "/productionPlan/ppRegister";
            }
            setTimeout(() => {
                $('#loadingModal').modal('hide');
            }, 500);
        },
        error: function(xhr, status, error) {
            alert("파일 업로드에 실패했습니다. : " + error);
            window.location.href = "/productionPlan/ppRegister";
        }
    });
});



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
        }
    });
});
