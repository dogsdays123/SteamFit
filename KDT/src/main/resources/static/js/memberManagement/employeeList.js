document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectList').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('modifyEmployeeModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectList:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 tr 가져오기

    const modalForm = document.querySelector('#ModifyModal form'); // 모달 폼 (ID는 실제 사용하는 폼의 ID로)

    // 이전에 추가된 동적 hidden input 제거
    modalForm.querySelectorAll('input.dynamic-hidden').forEach(input => input.remove());

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    // 선택된 사원 목록을 표시할 tbody
    const selectedEmpList = document.querySelector('.selectedEmpListMo');
    // 선택된 사원 목록을 초기화 (기존 내용 제거)
    selectedEmpList.innerHTML = '';

    selectedRows.forEach((row, index) => {
        const tdList = row.querySelectorAll('td');

        const uId = tdList[1].textContent.trim();
        const uName = tdList[2].textContent.trim();
        const userJob = tdList[3].querySelector('select') ? tdList[3].querySelector('select').value : '';
        const userRank = tdList[4].querySelector('select') ? tdList[4].querySelector('select').value : '';
        const regDate = tdList[5].textContent.trim();
        const status = tdList[6].querySelector('select') ? tdList[6].querySelector('select').value : '';

        const fields = {
            [`uId`]: uId,
            [`status`]: status,
            [`userRank`]: userRank,
            [`userJob`]: userJob
            //[`users[${index}].status`]: status
        };

        for (const [name, value] of Object.entries(fields)) {
            const hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = name;
            hidden.value = value;
            hidden.classList.add('dynamic-hidden');
            modalForm.appendChild(hidden);
        }

        // 새로운 tr 요소를 생성하고, 선택된 사원 정보를 추가
        const newRow = document.createElement('tr');
        newRow.innerHTML = `
            <td>${uId}</td>
            <td>${uName}</td>
            <td>${userJob}</td>
            <td>${userRank}</td>
            <td>${regDate}</td>
            <td>${status}</td>
        `;

        // 생성한 tr 요소를 tbody에 추가
        selectedEmpList.appendChild(newRow);
    });

    const modal = new bootstrap.Modal(document.getElementById('ModifyModal'));
    modal.show();
});

document.getElementById('removeEmployeeModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectList:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 tr 가져오기

    const modalForm = document.querySelector('#RemoveModal form'); // 모달 폼 (ID는 실제 사용하는 폼의 ID로)

    // 이전에 추가된 동적 hidden input 제거
    modalForm.querySelectorAll('input.dynamic-hidden').forEach(input => input.remove());

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    // 선택된 사원 목록을 표시할 tbody
    const selectedEmpList = document.querySelector('.selectedEmpListRm');
    // 선택된 사원 목록을 초기화 (기존 내용 제거)
    selectedEmpList.innerHTML = '';

    selectedRows.forEach((row, index) => {
        const tdList = row.querySelectorAll('td');

        const uId = tdList[1].textContent.trim();
        const uName = tdList[2].textContent.trim();
        const userJob = tdList[3].querySelector('select') ? tdList[3].querySelector('select').value : '';
        const userRank = tdList[4].querySelector('select') ? tdList[4].querySelector('select').value : '';
        const regDate = tdList[5].textContent.trim();
        const status = tdList[6].querySelector('select') ? tdList[6].querySelector('select').value : '';

        const fields = {
            [`uId`]: uId,
            [`status`]: status,
            [`userRank`]: userRank,
            [`userJob`]: userJob
            //[`users[${index}].status`]: status
        };

        for (const [name, value] of Object.entries(fields)) {
            const hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = name;
            hidden.value = value;
            hidden.classList.add('dynamic-hidden');
            modalForm.appendChild(hidden);
        }

        // 새로운 tr 요소를 생성하고, 선택된 사원 정보를 추가
        const newRow = document.createElement('tr');
        newRow.innerHTML = `
            <td>${uId}</td>
            <td>${uName}</td>
            <td>${userJob}</td>
            <td>${userRank}</td>
            <td>${regDate}</td>
            <td>${status}</td>
        `;

        // 생성한 tr 요소를 tbody에 추가
        selectedEmpList.appendChild(newRow);
    });

    const modal = new bootstrap.Modal(document.getElementById('RemoveModal'));
    modal.show();
});
