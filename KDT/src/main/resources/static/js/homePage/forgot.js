$(document).ready(function () {
    $('#checkPassword').on('click', function (e) {
        e.preventDefault();
        const email = $('#checkEmail').val().trim();

        if (!email) {
            alert("이메일을 입력해주세요.");
            return;
        }

        // 첫 번째 AJAX 요청: 이메일이 존재하는지 확인
        $.ajax({
            url: '/firstView/forgot/checkEmail',  // 이메일 존재 여부를 확인하는 URL
            type: 'POST',
            data: { checkEmail: email },  // 파라미터 이름을 'checkEmail'로 일치시킴
            success: function(response) {
                if (response.exists) {
                    // 이메일이 존재하면 두 번째 요청을 보냄
                    sendPasswordResetEmail(email);
                } else {
                    alert(response.msg);  // 'msg' 값을 이용한 오류 메시지 표시
                }
            },
            error: function(xhr, status, error) {
                alert("서버 오류: " + error);
            }
        });
    });

    // 두 번째 AJAX 요청: 비밀번호 재설정 이메일 전송
    function sendPasswordResetEmail(email) {
        $.ajax({
            url: '/firstView/forgot/sendResetLink',  // 비밀번호 재설정 링크 전송 URL
            type: 'POST',
            data: { checkEmail: email },
            beforeSend: function() {
                $('#loadingModal').modal('show');  // 로딩 모달 띄우기
            },
            success: function(response) {
                setTimeout(() => {
                    $('#makeAdminModal').modal('hide');
                }, 500); // 0.5초 후에 닫기
                alert(response.msg);
            },
            error: function(xhr, status, error) {
                setTimeout(() => {
                    $('#loadingModal').modal('hide');
                }, 500);
                alert("서버 오류: " + error);
            },
            complete: function() {
                $('#loadingModal').modal('hide');  // 로딩 모달 숨기기
            }
        });
    }
});
