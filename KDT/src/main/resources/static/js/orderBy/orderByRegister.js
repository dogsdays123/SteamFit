let orderByList = [];
let worldValue = [];
let dppCode;
let planForPDF = [];
let sName;
let uId;
let dppNum = 0;
let orderNum = 0;

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('orderByForm');
    const buttons = document.querySelectorAll('.openOrderByModal');
    const modalElement = document.getElementById('orderByModal');
    const tbody = document.getElementById('orderByBody');

    buttons.forEach(function (button) {
        button.addEventListener('click', function () {

            // 클릭된 버튼의 부모 <tr>을 찾음
            const row = button.closest('tr');
            const dppCode1 = row.children[0].textContent.trim();
            const leadTime = row.children[1].textContent.trim();
            const pName = row.children[2].textContent.trim();
            const sName1 = row.children[3].textContent.trim();
            const mName = row.children[4].textContent.trim();
            const dppNum1 = row.children[5].textContent.trim();
            const rqNum = row.children[6].textContent.trim();
            const today = new Date().toISOString().split('T')[0];
            uId = row.children[9].textContent.trim();

            document.getElementById('payDate').value = today;
            sName = sName1;
            dppCode = dppCode1;
            dppNum = parseInt(dppNum1, 10);

            // 모달에 값 주입
            document.getElementById('dppCodeInput').textContent = dppCode1;
            document.getElementById('pNameInput').textContent = pName;
            document.getElementById('mNameInput').textContent = mName;
            document.querySelector('.dppNumInput').value = dppNum;
            document.querySelector('.rqNumInput').value = rqNum;
            document.querySelector('.mPerPrice').value = document.querySelector('.mPerPriceHidden').value;

            // 모달 열기
            const modal = new bootstrap.Modal(document.getElementById('orderByModal'));
            modal.show();
        });
    });

    //모든 폼 자동제출 막기
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && e.target.tagName === 'INPUT') {
                e.preventDefault();
            }
        });
    });

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        alert(`구매 발주가 등록되었습니다`);

        // 폼을 서버에 전송하는 로직
        form.submit(); // 수동으로 다시 제출

        const modal = bootstrap.Modal.getInstance(document.getElementById('orderByModal'));
        if (modal) modal.hide();

        if (tbody) tbody.innerHTML = '';
        orderByList.length = 0;
    });
});

function addOrderBy(button) {
    const container = button.closest('.tab-pane'); // 현재 탭 안

    const textInputs = container.querySelectorAll('input[type="text"]');
    const numberInputs = container.querySelectorAll('input[type="number"]');
    const selectElements = container.querySelectorAll('select');
    const dateInputs = container.querySelectorAll('input[type="date"]');
    const radioInputs = container.querySelectorAll('input[type="radio"]');

    const mName = document.getElementById('mNameInput').textContent;

// text
    const perPrice = textInputs.length > 2 ? textInputs[2].value.trim() : '';
    const oRemarks = document.getElementById('oRemarks').value;
    const orderAddress = textInputs.length > 4 ? textInputs[4].value.trim() : '';

// number
    const oNum = numberInputs.length > 0 ? numberInputs[0].value.trim() : '';
    const oTotalPrice = numberInputs.length > 1 ? numberInputs[1].value.trim() : '';

// date
    const oExpectDate = dateInputs.length > 0 ? dateInputs[0].value.trim() : '';
    const payDate = dateInputs.length > 1 ? dateInputs[1].value.trim() : '';

// radio (하나만 선택되도록 그룹화된 라디오에서 선택된 값 찾기)
    const payMethod = container.querySelector('input[name="payMethods"]:checked')?.value || '';
    const payDocument = container.querySelector('input[name="payDocuments"]:checked')?.value || '';

// 유효성 체크
    if (!oNum || !oTotalPrice || !oExpectDate || !perPrice || !orderAddress || !payDate ||
        !payMethod || !payDocument) {
        alert('필수 정보를 입력해주세요');
        return;
    }


    // 데이터 객체 생성
    const item = {
        mName,
        oNum,
        oExpectDate,
        sName,
        orderAddress,
        oRemarks,
        payDate,
        perPrice,
        oTotalPrice,
        payMethod,
        payDocument
    };
    const item2 = {
        dppCode,
        oNum,
        oExpectDate,
        sName,
        orderAddress,
        oRemarks,
        payDate,
        payMethod,
        payDocument,
        uId
    };

    planForPDF.push(item2);

    console.log(planForPDF);

    orderByList.push(item);

    renderOrderByTable(false);
}


function renderOrderByTable(check) {
    const tbody = document.getElementById('orderByBody');
    tbody.innerHTML = ''; // 초기화
    let tempOrderNum = 0;

    orderByList.forEach((item, index) => {
        const currentNum = parseInt(item.oNum, 10);

        if (!check) {
            if (tempOrderNum + currentNum > dppNum) {
                alert("조달 수량을 초과하여 주문할 수 없습니다.");
                removeOrderBy(index, 0);
                return; // 이 항목은 건너뜀
            }
            tempOrderNum += currentNum;
        }

        const row = document.createElement('tr');

        //이런식으로 input값 넣어줄거임
        row.innerHTML = `
      <td>
      <input type="hidden" name="orders[${index}].dppCode" value="${dppCode}">
      <input type="hidden" name="orders[${index}].oNum" value="${item.oNum}">
      <input type="hidden" name="orders[${index}].orderAddress" value="${item.orderAddress}">
      <input type="hidden" name="orders[${index}].payDate" value="${item.payDate}">
      <input type="hidden" name="orders[${index}].payMethod" value="${item.payMethod}">
      <input type="hidden" name="orders[${index}].payDocument" value="${item.payDocument}">
        ${item.oNum}
        </td>
      <td><input type="hidden" name="orders[${index}].oTotalPrice" value="${item.oTotalPrice}">${item.oTotalPrice}</td>
      <td><input type="hidden" name="orders[${index}].oExpectDate" value="${item.oExpectDate}">${item.oExpectDate}</td>
      <td><input type="hidden" name="orders[${index}].oRemarks" value="${item.oRemarks}">${item.oRemarks}</td>
      <td class="text-center">
        <button class="btn btn-sm btn-outline-danger" onclick="removeOrderBy(${index}, ${item.oNum})">삭제</button>
      </td>
      <td>
    <button type="button" class="btn btn-outline-primary btn-sm" onclick="previewOrderPDF(${index})">미리보기</button>
    </td>
    `;
        tbody.appendChild(row);
    });
    if (!check) {
        orderNum = tempOrderNum; // 실제 누적은 여기서 반영
    }
}

function removeOrderBy(index, oNum) {
    orderNum -= parseInt(oNum, 10);
    planForPDF.splice(index, 1);
    orderByList.splice(index, 1);
    renderOrderByTable(true);
}

function resetView() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('orderByModal'));

    const tbody = document.getElementById('orderByBody');
    if (tbody) tbody.innerHTML = '';
    orderByList.length = 0;

    if (modal) modal.hide();
}

$(document).ready(function () {

    $('#orderInput').on('input', function () {
        const orderInput = $(this).val();  // 선택된 상품 값
        let total;

        if (!orderInput || isNaN(orderInput)) return;

        const unitPrice = parseFloat($('.mPerPrice').val());  // 단가
        if (isNaN(unitPrice)) return;

        // orderInput이 dppNum보다 큰 경우 경고 메시지 출력하고 값을 제한
        if (orderInput > dppNum) {
            alert(`입력값은 ${dppNum}보다 클 수 없습니다.`);
            $(this).val(dppNum);
            total = unitPrice * dppNum;
        } else {
            total = unitPrice * orderInput;
        }

        const totalPrice = $('#totalPrice');
        totalPrice.val(total);  // input 요소일 경우
        totalPrice.trigger('change');  // 리스너가 있을 경우만 유효
    });
});


//PDF용
function previewOrderPDF(index) {
    const selectedPlans = planForPDF;

    if (selectedPlans.length === 0) {
        alert('발주를 선택해주세요.');
        return;
    }

    // const formData = collectFormData();
    const formData = {
        pdfs: [selectedPlans[index]]
    };

    fetch('/orderBy/pdf/preview', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(formData)
    })
        .then(res => res.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            window.open(url, '_blank'); // 새 창으로 PDF 미리보기
        })
        .catch(() => alert('미리보기에 실패했습니다.'));
}


function generateOrderPDF(index) {
    const selectedPlans = planForPDF;

    if (selectedPlans.length === 0) {
        alert('조달계획을 선택해주세요.');
        return;
    }

    // const formData = collectFormData();
    const formData = {
        pdfs: [selectedPlans[index]]
    };

    fetch('/orderBy/pdf/download', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(formData)
    })
        .then(res => res.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `발주서_${formData.planCode}.pdf`;
            a.click();
            alert('발주서가 PDF로 생성되어 메일로 전송되었습니다.');
            const modal = bootstrap.Modal.getInstance(document.getElementById('purchaseOrderModal'));
            if (modal) modal.hide();
        })
        .catch(() => alert('발주서 생성에 실패했습니다.'));
}
