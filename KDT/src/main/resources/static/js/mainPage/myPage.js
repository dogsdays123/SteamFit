document.addEventListener("DOMContentLoaded", function () {
    const modifyModal = document.getElementById('modifyModal');
    const modifySupplierModal = document.getElementById('modifySupplierModal');
    const removeModal = document.getElementById('removeModal');
    const removeButton = document.getElementById('removeButton');

    // modifySupplierModal이 존재하는지 확인한 후에 이벤트 추가
    if (modifySupplierModal) {
        modifySupplierModal.addEventListener('click', function () {
            const modal = new bootstrap.Modal(document.getElementById('modifySupplier'));
            modal.show();
        });
    }

    // modifySupplierModal이 존재하는지 확인한 후에 이벤트 추가
    if (modifyModal) {
        modifyModal.addEventListener('click', function () {
            const modal = new bootstrap.Modal(document.getElementById('modify'));
            modal.show();
        });
    }

    if(removeModal){
        removeModal.addEventListener('click', function () {
            const modal = new bootstrap.Modal(document.getElementById('remove'));
            modal.show();
        });
    }

    if(removeButton){
        removeButton.addEventListener('click', function () {
            // AJAX 요청 보내기
            $.ajax({
                url: '/mainPage/remove', // Controller의 URL로 수정 (예: '/user/checkId')
                type: 'POST',
                success: function() {
                    alert("회원정보가 삭제되었습니다.");
                    window.location.replace("/logout");
                },
                error: function(xhr, status, error) {
                    alert("연관된 정보가 있습니다.");
                    window.location.replace("/logout");
                }
            });
        });
    }
});


let checkAll = { idCheck: false, emailCheck: false };
var signupButton = document.getElementById('signupButton');

$(document).ready(function() {
    $('#uEmail').on('input', function(e) {

        var textarea = document.getElementById('email-feedback'); //제한 텍스트
        var uEmail = $(this).val(); // 입력된 아이디 값을 가져옵니다.
        var uId = $('#uId').val();

        const eventCheck = new Event('input', {bubbles: true});

        // 이메일 값이 비어 있지 않은지 확인
        if (uEmail.trim() === "") {
            textarea.classList.remove('text-success');
            textarea.classList.add('text-danger');
            textarea.value = '이메일을 입력해 주세요.';
            checkAll.emailCheck = false;
            signupButton.dispatchEvent(eventCheck);
            return;
        } else{
            // 정규식 체크: @와 .을 포함한 이메일 형식
            var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!regex.test(uEmail)) {
                textarea.classList.remove('text-success');
                textarea.classList.add('text-danger');
                textarea.value = '유효하지 않은 이메일 형식입니다.';
                checkAll.emailCheck = false;
                signupButton.dispatchEvent(eventCheck);
                return;
            }
        }

        // AJAX 요청 보내기
        $.ajax({
            url: '/mainPage/checkEmail', // Controller의 URL로 수정 (예: '/user/checkId')
            type: 'POST',
            data: { uEmail: uEmail,
                uId: uId}, // 전송할 데이터
            success: function(response) {
                // 서버에서 응답이 성공적으로 왔을 때 처리
                if (response.isAvailable) {
                    textarea.value = "사용 가능한 이메일입니다.";
                    textarea.classList.add('text-success'); // 새 클래스를 추가
                    textarea.classList.remove('text-danger'); // 기존 클래스를 제거
                    checkAll.emailCheck = true;
                } else {
                    textarea.value = "이미 존재하는 이메일입니다.";
                    textarea.classList.remove('text-success'); // 새 클래스를 추가
                    textarea.classList.add('text-danger'); // 기존 클래스를 제거
                    checkAll.emailCheck = false;
                }
                var event = new Event('input', {bubbles: true});
                textarea.dispatchEvent(event);

                signupButton.dispatchEvent(eventCheck);

            },
            error: function(xhr, status, error) {
                alert("서버 오류" + error);
            }
        });
    });
});

//비밀번호 확인
$(document).ready(function() {
    $('#uPassword, #aPswCheck').on('input', function() {
        var uPassword = $('#uPassword').val();  // 비밀번호 입력 값
        var passwordCheck = $('#aPswCheck').val();  // 비밀번호 확인 입력 값
        var feedback = $('#password-feedback');  // 비밀번호 확인 메시지

        // 비밀번호가 일치하면
        if(uPassword.length < 6 || uPassword.length > 16){
            feedback.removeClass('text-success').addClass('text-danger');
            feedback.text('비밀번호의 길이가 올바르지 않습니다.');
            return;
        } else{
            if (uPassword === passwordCheck) {
                feedback.removeClass('text-danger').addClass('text-success');
                feedback.text('비밀번호가 일치합니다.');
            } else {
                feedback.removeClass('text-success').addClass('text-danger');
                feedback.text('비밀번호가 일치하지 않습니다.');
            }
        }
    });
});