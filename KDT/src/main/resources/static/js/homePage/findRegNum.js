function checkInput_null(fieldIds) {
    var fields = fieldIds.split(",");
    for (var i = 0; i < fields.length; i++) {
        var field = document.getElementById(fields[i]);
        if (!field || field.value.trim() === "") {
            alert(field.alt + "을(를) 입력해주세요.");
            field.focus();
            return false;
        }
    }
    return true;
}

function code_check() {
    if (!checkInput_null("c1,c2,c3")) {
        return;
    }

    const code = document.getElementById("c1").value +
        document.getElementById("c2").value +
        document.getElementById("c3").value;

    const data = { "b_no": [code] };

    document.getElementById("loadingModal").style.display = "block";

    $.ajax({
        url: "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=LrTpLPKTnvj5yzmwkL75xRIB9O2U312Ev%2F8KItvAzmjHhOUBRtJerxRdShcm%2FiitivEMqUsKxDrla%2FNi2%2BSfoA%3D%3D",
        type: "POST",
        data: JSON.stringify(data),
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        success: function (result) {
            document.getElementById("loadingModal").style.display = "none";

            if (result.match_cnt === 1) {
                const biz = result.data[0];

                // hidden 필드에 값 넣기
                document.getElementById("b_no").value = biz.b_no;
                document.getElementById("b_stt").value = biz.b_stt;
                document.getElementById("tax_type").value = biz.tax_type;
                document.getElementById("rbf_tax_type").value = biz.rbf_tax_type;
                document.getElementById("utcc_yn").value = biz.utcc_yn;

                // 보여지는 필드에 반영
                const regNumField = document.getElementById("sRegNum");
                if (regNumField) {
                    regNumField.value = biz.b_no;
                    regNumField.dispatchEvent(new Event('input', { bubbles: true }));
                }

                alert("올바른 사업자번호입니다.\n" + biz.tax_type);
            } else {
                alert("등록되지 않은 사업자번호입니다.");
            }
        },
        error: function (err) {
            document.getElementById("myModal").style.display = "none";
            console.error("에러:", err.responseText || err);
            alert("API 요청 중 오류가 발생했습니다.");
        }
    });
}
