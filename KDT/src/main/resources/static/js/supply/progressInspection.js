let psList = [];

document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('openPsModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기
    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const tbody = document.getElementById('psBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
        `;
        psList.push(Array.from(cells).map(cell => cell.textContent.trim()));
        tbody.appendChild(newRow);
    });

    const modal = new bootstrap.Modal(document.getElementById('psModal'));
    modal.show();
});

function addPs(){
    const tbody = document.getElementById('psHiddenBody');
    tbody.innerHTML = ''; // 초기화
    let rowIndex = 0;
    const psDate = document.getElementById('psDate').value;
    const psRemarks = document.getElementById('psRemarks').value;

    psList.forEach((Array) => {
        const row = document.createElement('div');
        //이런식으로 input값 넣어줄거임
        row.innerHTML = `
      <input type="hidden" name="pss[${rowIndex}].psNum" value="${Array[4]}">
      <input type="hidden" name="pss[${rowIndex}].oCode" value="${Array[1]}">
      <input type="hidden" name="pss[${rowIndex}].uId" value="${Array[9]}">
      <input type="hidden" name="pss[${rowIndex}].psDate" value="${psDate}">
      <input type="hidden" name="pss[${rowIndex}].psRemarks" value="${psRemarks}">
    `;
        tbody.appendChild(row);
        rowIndex++;
    });
}