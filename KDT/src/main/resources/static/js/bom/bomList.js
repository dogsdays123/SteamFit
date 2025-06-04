let worlds = [];

document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr');

        const bId = row.querySelector('td:nth-child(2)').innerText;
        const pName = row.querySelector('td:nth-child(3)').innerText;
        const mType = row.querySelector('td:nth-child(4)').innerText;
        const mName = row.querySelector('td:nth-child(5)').innerText;
        const require = row.querySelector('td:nth-child(6)').innerText;
        const uId = row.querySelector('td:nth-child(8)').innerText;


        document.getElementById('bId').value = bId;
        document.getElementById('pName').value = pName;
        document.getElementById('mType').value = mType;
        document.getElementById('mName').value = mName;
        document.getElementById('require').value = require;
        document.getElementById('uId').value = uId;

        worlds[0] = pName;
        worlds[1] = mType;
        loadComponentTypes(pName);

        // 모달 띄우기
        const modal = new bootstrap.Modal(document.getElementById('bomModify'));
        modal.show();
    });
});


document.getElementById('bomRemoveButton').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = '';

    const bIds = [];

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const bId = cells[1].textContent.trim();
        bIds.push(bId);

        newRow.innerHTML = `
            <td class="d-none">${cells[1].textContent.trim()}</td>   
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('bomRemoveModal');
    form.querySelectorAll('input[name="bIds"]').forEach(input => input.remove());

    bIds.forEach(bId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'bIds';
        hiddenInput.value = bId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('bomRemove'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/bom/bomList'
}, false)

$(document).ready(function () {

    // 선택 변경 시에도 동작
    $('[name="pName"]').on('change', function () {
        const input = $(this).val();
        worlds[1] = input;
        loadComponentTypes(input, "out");
    });

    $('[name="componentType"]').on('change', function () {
        const input = $(this).val();
        loadMName(worlds[1], input, "out");
    });

    // 선택 변경 시에도 동작
    $('#pName').on('change', function () {
        const input = $(this).val();
        worlds[0] = input;
        loadComponentTypes(input, "in");
    });

    $('#componentType').on('change', function () {
        const input = $(this).val();
        loadMName(worlds[0], input, "in");
    });
});

function loadComponentTypes(pName, position) {

    let innerValue;

    if(pName){
    switch (position){
        case "out": innerValue = "[name=\"componentType\"]"; break;
        case "in" : innerValue = "#componentType"; break;
    }

    const pNameEncode = encodeURIComponent(pName);

    $.ajax({
        url: `/bom/${pNameEncode}/forMType`,
        method: 'GET',
        success: function (componentTypes) {

            const cType = $(`${innerValue}`);
            cType.empty();
            cType.append('<option value="" selected>전체</option>');
            componentTypes.forEach(type => {
                cType.append(`<option value="${type}">${type}</option>`);
            });
            cType.trigger('change');
        },
        error: function (error) {
            console.error('부품 목록을 가져오는 중 오류 발생:', error);
        }
    });
    } else{
        $('#componentType').empty();  // 부품 목록 초기화
        var mComponentListHTML = $('#mComponentListHTML').html();  // 서버에서 렌더링된 HTML 가져오기
        $('#componentType').append(mComponentListHTML);  // mNameList의 option을 append
        $('#componentType').trigger('change');  // 변경 이벤트 트리거
    }
}

function loadMName(pName, mType, position) {

    let innerValue;

    switch (position){
        case "out": innerValue = "[name=\"mName\"]"; break;
        case "in" : innerValue = "#mName"; break;
    }

    if(mType && pName){
    const encode = [];
    encode[0] = encodeURIComponent(pName);
    encode[1] = encodeURIComponent(mType);

    $.ajax({
        url: `/bom/${encode[0]}/${encode[1]}/forMName`,
        method: 'GET',
        success: function (mNames) {

            const mName = $(`${innerValue}`);
            mName.empty();
            mName.append('<option value="" selected>전체</option>');
            mNames.forEach(type => {
                mName.append(`<option value="${type}">${type}</option>`);
            });
            mName.trigger('change');
        },
        error: function (error) {
            console.error('부품 목록을 가져오는 중 오류 발생:', error);
        }
    });
    }
}