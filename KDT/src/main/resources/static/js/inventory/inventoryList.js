document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

        // 각 td 값을 가져오기
        const isId = row.querySelector('td:nth-child(2)').innerText;
        const pCode = row.querySelector('td:nth-child(3)').innerText;
        const pName = row.querySelector('td:nth-child(4)').innerText;
        const cType = row.querySelector('td:nth-child(5)').innerText;
        const mCode = row.querySelector('td:nth-child(6)').innerText;
        const mName = row.querySelector('td:nth-child(7)').innerText;
        const isNum = row.querySelector('td:nth-child(8)').innerText;
        const isAvailable = row.querySelector('td:nth-child(9)').innerText;
        const isLocation = row.querySelector('td:nth-child(10)').innerText;


        document.getElementById('isId').value = isId;
        document.getElementById('pCode').value = pCode;
        document.getElementById('pName').value = pName;
        document.getElementById('mCode').value = mCode;
        document.getElementById('isNum').value = isNum;
        document.getElementById('isAvailable').value = isAvailable;
        document.getElementById('isLocation').value = isLocation;

        fetch(`/bom/api/products/${pCode}/component-types`)
            .then(res => res.json())
            .then(componentTypes => {
                const cTypeSelect = document.getElementById("cType");
                cTypeSelect.innerHTML = '<option value="">선택</option>';
                componentTypes.forEach(type => {
                    let option = document.createElement("option");
                    option.value = type;
                    option.textContent = type;
                    cTypeSelect.appendChild(option);
                });


                cTypeSelect.value = cType;

                // cType 변경 시 자재 목록 불러오기
                cTypeSelect.addEventListener("change", function() {
                    const selectedCType = this.value;
                    document.getElementById('mCode').value = '';
                    if (selectedCType) {
                        fetch(`/bom/api/materials?componentType=${selectedCType}`)
                            .then(res => res.json())
                            .then(materials => {
                                const mNameSelect = document.getElementById('mName');
                                mNameSelect.innerHTML = '<option value="">선택</option>';

                                materials.forEach(material => {
                                    console.log(material);
                                    const option = document.createElement('option');
                                    option.value = material.mcode;
                                    option.textContent = material.mname;
                                    option.setAttribute("data-name", material.mname);
                                    option.setAttribute("data-code", material.mcode);
                                    mNameSelect.appendChild(option);
                                });

                                mNameSelect.replaceWith(mNameSelect.cloneNode(true));
                                const freshSelect = document.getElementById("mName");

                                freshSelect.addEventListener("change", function () {
                                    const selectedMaterial = this.options[this.selectedIndex];
                                    const materialCode = selectedMaterial.getAttribute("data-code");
                                    console.log("선택된 mcode:", materialCode);
                                    document.getElementById("mCode").value = materialCode;
                                });

                                const selectedOption = Array.from(mNameSelect.options).find(option => option.textContent === mName);
                                if (selectedOption) {
                                    mNameSelect.value = selectedOption.value;
                                } else {
                                    mNameSelect.value = materials.length > 0 ? materials[0].mcode : '';
                                }
                            })
                            .catch(error => console.error('Error fetching materials:', error));
                    }
                });

                // 최초 로드시, cType에 맞는 자재 목록 불러오기
                if (cType) {
                    fetch(`/bom/api/materials?componentType=${cType}`)
                        .then(res => res.json())
                        .then(materials => {
                            const mNameSelect = document.getElementById('mName');
                            mNameSelect.innerHTML = '<option value="">선택</option>';

                            materials.forEach(material => {
                                console.log(materials);
                                const option = document.createElement('option');
                                option.value = material.mcode;
                                option.textContent = material.mname;
                                option.setAttribute("data-name", material.mname);
                                option.setAttribute("data-code", material.mcode);
                                mNameSelect.appendChild(option);
                            });

                            const selectedOption = Array.from(mNameSelect.options).find(option => option.textContent === mName);
                            if (selectedOption) {
                                mNameSelect.value = selectedOption.value;
                                document.getElementById("mCode").value = selectedOption.getAttribute("data-code");
                            } else {
                                mNameSelect.value = materials.length > 0 ? materials[0].mcode : '';
                                document.getElementById("mCode").value = materials.length > 0 ? materials[0].mcode : '';
                            }

                            mNameSelect.addEventListener('change', function () {
                                const selectedMaterial = this.options[this.selectedIndex];
                                const materialCode = selectedMaterial.getAttribute("data-code");
                                document.getElementById("mCode").value = materialCode;
                            });

                        })
                        .catch(error => console.error('Error fetching materials:', error));
                }
            })
            .catch(error => console.error('Error fetching component types:', error));

        // 모달 띄우기
        const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
        modal.show();
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
        const mLeadTime = row.querySelector('td:nth-child(14)').innerText;


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
        document.getElementById('ppMLeadTime').value = mLeadTime;

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

    const isIds = [];

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const isId = cells[1].textContent.trim();
        isIds.push(isId);

        newRow.innerHTML = `
            <td style="display: none">${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
            <td>${cells[8].textContent.trim()}</td>
            <td>${cells[9].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="isIds"]').forEach(input => input.remove());

    isIds.forEach(isId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'isIds';
        hiddenInput.value = isId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/inventory/inventoryList'
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