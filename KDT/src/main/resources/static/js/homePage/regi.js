let checkAll = { idCheck: false, emailCheck: false };
var signupButton = document.getElementById('signupButton');

$(document).ready(function() {
    $('#uId').on('input', function(e) {

        var textarea = document.getElementById('username-feedback'); //제한 텍스트
        var uId = $(this).val(); // 입력된 아이디 값을 가져옵니다.

        const eventCheck = new Event('input', {bubbles: true});

        // 아이디 값이 비어 있지 않은지 확인
        if (uId.trim() === "") {
            textarea.classList.remove('text-success');
            textarea.classList.add('text-danger');
            textarea.value = '아이디를 입력해 주세요.';
            checkAll.idCheck = false;
            toggleSignupButton();
            signupButton.dispatchEvent(eventCheck);
            return;
        } else{
            // 정규식 체크: 3자 이상, 16자 이하, 영어/숫자만 가능
            var regex = /^[a-zA-Z0-9]{3,16}$/;
            if (!regex.test(uId)) {
                textarea.classList.remove('text-success');
                textarea.classList.add('text-danger');
                textarea.value = '아이디는 3자 이상, 16자 이하의 영어 또는 숫자만 가능';
                checkAll.idCheck = false;
                toggleSignupButton();
                signupButton.dispatchEvent(eventCheck);
                return;
            }
        }

        // AJAX 요청 보내기
        $.ajax({
            url: '/firstView/checkId', // Controller의 URL로 수정 (예: '/user/checkId')
            type: 'POST',
            data: { uId: uId }, // 전송할 데이터
            success: function(response) {
                // 서버에서 응답이 성공적으로 왔을 때 처리
                if (response.isAvailable) {
                    textarea.value = "사용 가능한 아이디입니다.";
                    textarea.classList.add('text-success'); // 새 클래스를 추가
                    textarea.classList.remove('text-danger'); // 기존 클래스를 제거
                    checkAll.idCheck = true;
                } else {
                    textarea.value = "이미 존재하는 아이디입니다.";
                    textarea.classList.remove('text-success'); // 새 클래스를 추가
                    textarea.classList.add('text-danger'); // 기존 클래스를 제거
                }
                var event = new Event('input', {bubbles: true});
                textarea.dispatchEvent(event);

                toggleSignupButton();
                signupButton.dispatchEvent(eventCheck);
            },
            error: function(xhr, status, error) {
                alert("서버 오류" + error);
            }
        });
    });
});

$(document).ready(function() {
    $('#uEmail').on('input', function(e) {

        var textarea = document.getElementById('email-feedback'); //제한 텍스트
        var uEmail = $(this).val(); // 입력된 아이디 값을 가져옵니다.

        const eventCheck = new Event('input', {bubbles: true});

        // 이메일 값이 비어 있지 않은지 확인
        if (uEmail.trim() === "") {
            textarea.classList.remove('text-success');
            textarea.classList.add('text-danger');
            textarea.value = '이메일을 입력해 주세요.';
            checkAll.emailCheck = false;
            toggleSignupButton();
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
                toggleSignupButton();
                signupButton.dispatchEvent(eventCheck);
                return;
            }
        }

        // AJAX 요청 보내기
        $.ajax({
            url: '/firstView/checkEmail', // Controller의 URL로 수정 (예: '/user/checkId')
            type: 'POST',
            data: { uEmail: uEmail }, // 전송할 데이터
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

                toggleSignupButton();
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


//레디오 클릭 유저타입
$(document).ready(function() {
    // 처음 페이지 로드 시, 선택된 radio에 따라 div를 표시
    $('input[name="userType"]').on('change', function() {
        var userType = $('input[name="userType"]:checked').val();

        const supplierInputs = document.querySelectorAll('.supplier');
        const userInputs = document.querySelectorAll('.user');


        if (userType === 'other') {
            // '협력회사' 선택 시
            supplierInputs.forEach(input => {
                input.required = true;
            });
            userInputs.forEach(input => {
                input.required = false;
            });

            $('#sup').show(); // 주소 입력 필드 보이기
            $('#user').hide(); // 생년월일 필드 숨기기
        } else if (userType === 'our') {
            // 'PNS' 선택 시
            supplierInputs.forEach(input => {
                input.required = false;
            });
            userInputs.forEach(input => {
                input.required = true;
            });

            $('#sup').hide(); // 주소 입력 필드 숨기기
            $('#user').show(); // 생년월일 필드 보이기
        }

        // AJAX 요청 보내기
        $.ajax({
            url: '/firstView/checkType', // Controller의 URL로 수정 (예: '/user/checkId')
            type: 'POST',
            data: { userType: userType }, // 전송할 데이터
            success: function(response) {
                // 서버에서 응답이 성공적으로 왔을 때 처리
                if (response.isAvailable) {
                } else {
                }
            },
            error: function(xhr, status, error) {
                alert("서버 오류" + error);
            }
        });
    });
});

// 버튼의 상태를 업데이트하는 함수
function toggleSignupButton() {
    // idCheck와 emailCheck가 모두 true일 때만 버튼을 활성화
    if (checkAll.idCheck && checkAll.emailCheck) {
        signupButton.disabled = false;  // 아이디와 이메일이 모두 유효하면 버튼 활성화
    } else {
        signupButton.disabled = true;   // 하나라도 비어 있으면 버튼 비활성화
    }
}

function makeAdmin(){
    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/firstView/admin`,  // URL 인코딩 적용
        method: 'POST',  // HTTP GET 요청
        beforeSend: function() {
            $('#makeAdminModal').modal('show');  // 로딩 모달 띄우기
        },
        success: function () {
            alert("데이터 생성 완료")
            setTimeout(() => {
                $('#makeAdminModal').modal('hide');
            }, 500); // 0.5초 후에 닫기
        },
        error: function (error) {
            alert("이미 생성된 데이터입니다.")
            console.error('errorMessage :', error);
            window.location.href = '/firstView/login';
        }
    });
}