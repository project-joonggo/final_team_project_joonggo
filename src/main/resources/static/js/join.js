// 회원가입 필드
const email = document.getElementById('i');
const userName = document.getElementById('userName');
const postCode = document.getElementById('postCode');
const address1 = document.getElementById('address1');
const address3 = document.getElementById('address3');
let passwordFinalCheck = false;

const passwordInput = document.getElementById('password');
const passwordCheckInput = document.getElementById('passwordCheck');
const passwordCheckMessage = document.getElementById('passwordCheckMessage');
const passwordMessage = document.getElementById('passwordMessage');
const joinBtn = document.getElementById('joinPostBtn');

const finalMessage = document.getElementById("finalMessage");

// 비밀번호 정규식 (대소문자/숫자/특수문자 3가지 이상, 8~16자)
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,16}$/;

// 비밀번호 입력 시 체크
passwordInput.addEventListener('input', function() {
    const password = passwordInput.value;

// 비밀번호 규칙에 맞지 않으면 메시지 표시
    if (!passwordRegex.test(password)) {
        passwordMessage.classList.remove('success');
        passwordMessage.classList.add('error');
        passwordMessage.textContent = '비밀번호는 영문 대소문자, 숫자, 특수문자 3가지 이상 조합, 8~16자 이내로 작성해주세요.';
    } else {
        passwordMessage.classList.remove('error');
        passwordMessage.classList.add('success');
        passwordMessage.textContent = '적합한 비밀번호입니다.';
    }

    // 비밀번호 확인 자동 입력 제거
    passwordCheckInput.value = '';

    // 비밀번호 확인 체크
    validatePasswordMatch();
});

// 비밀번호 확인이 일치하는지 확인
passwordCheckInput.addEventListener('input', validatePasswordMatch);

function validatePasswordMatch() {
    const password = passwordInput.value;
    const passwordCheck = passwordCheckInput.value;

    // 비밀번호 확인이 일치하는지, 비밀번호 정규식이 만족하는지 확인
    if (password !== passwordCheck || !passwordRegex.test(password)) {
        passwordCheckMessage.classList.remove('success');
        passwordCheckMessage.classList.add('error');
        passwordCheckMessage.textContent = '비밀번호가 일치하지 않거나 규칙을 만족하지 않습니다.';
        passwordFinalCheck = false;
    } else {
        passwordCheckMessage.classList.remove('error');
        passwordCheckMessage.classList.add('success');
        passwordCheckMessage.textContent = '비밀번호가 일치합니다.';
        passwordFinalCheck = true;
    }
}

//[ Phone Check Num ]*/
let authCode = "";
let checkNum = false;

// 전화번호 인증 버튼 클릭 이벤트
document.getElementById("phoneChk").addEventListener("click", function () {
    const isValidPhone = true; // 전화번호 검증 여부를 나타내는 변수 (필요시 실제 검증 로직 추가)
    const phoneInput = document.getElementById("phone");
    const checkInput = document.getElementById("check");
    const dupCheck = document.getElementById("dup-check");

    if (!isValidPhone) {
        alert("휴대폰 번호가 올바르지 않습니다.");
    } else {
        alert("인증번호 발송이 완료되었습니다.\n휴대폰에서 인증번호 확인을 해주십시오.");
        const pn = phoneInput.value;

        fetch(`/user/phoneCheck?phone=${pn}`, { method: "GET", cache: "no-cache" })
            .then((response) => response.text())
            .then((data) => {
                if (data === "error") {
                    alert("휴대폰 번호가 올바르지 않습니다.");
                } else {
                    checkInput.disabled = false;
                    dupCheck.textContent = "인증번호를 입력한 뒤 인증하기를 눌러주십시오.";
                    dupCheck.style.color = "red";
                    phoneInput.readOnly = true;
                    authCode = data;
                }
            })
            .catch((error) => {
                console.error("Error:", error);
            });
    }
});

// 인증번호 확인 버튼 클릭 이벤트
document.getElementById("phoneChk2").addEventListener("click", function () {
    const checkInput = document.getElementById("check");
    const dupCheck = document.getElementById("dup-check");

    if (authCode.length <= 0) {
        checkInput.disabled = false;
        dupCheck.textContent = "인증번호를 입력한 뒤 인증하기를 눌러주십시오.";
        dupCheck.style.color = "red";
    } else if (checkInput.value === authCode) {
        dupCheck.textContent = "인증번호가 일치합니다.";
        dupCheck.style.color = "green";
        checkInput.disabled = true;
        checkNum = true;
    } else {
        dupCheck.textContent = "인증번호가 일치하지 않습니다. 확인해주시기 바랍니다.";
        dupCheck.style.color = "red";
        checkInput.focus();
        checkNum = false;
        return;
    }
});

// 우편번호 찾기
function searchPostCode(){
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if(data.userSelectedType === 'R'){
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                document.getElementById("address3").value = extraAddr;

            } else {
                document.getElementById("address3").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('postCode').value = data.zonecode;
            document.getElementById("address1").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("address2").focus();
        }
    }).open();
}

// 회원가입 필드

/*if(email.value === ''){
    finalMessage.textContent = "아이디(이메일)을 입력해 주세요.";
} else if (userName.value === ''){
    finalMessage.textContent = "이름을 입력해 주세요.";
} else if (postCode.value === ''){
    finalMessage.textContent = "우편번호를 입력해 주세요.";
} else if (address1.value === '' || address3.value){
    finalMessage.textContent = "주소를 입력해 주세요."
} else if (passwordFinalCheck === false){
    finalMessage.textContent = "비밀번호를 입력해 주세요.";
} else if (checkNum === false){
    finalMessage.textContent = "휴대폰 번호를 인증해 주세요.";
} else {
    joinBtn.disabled = false;
}*/

function validateForm() {
    // 유효성 검사 로직
    if (email.value === '') {
        finalMessage.textContent = "아이디(이메일)을 입력해 주세요.";
    } else if (userName.value === '') {
        finalMessage.textContent = "이름을 입력해 주세요.";
    } else if (postCode.value === '') {
        finalMessage.textContent = "우편번호를 입력해 주세요.";
    } else if (address1.value === '' || address3.value === '') {
        finalMessage.textContent = "주소를 입력해 주세요.";
    } else if (!passwordFinalCheck) {
        finalMessage.textContent = "비밀번호를 입력해 주세요.";
    } else if (!checkNum) {
        finalMessage.textContent = "휴대폰 번호를 인증해 주세요.";
    } else {
        finalMessage.textContent = ''; // 오류 메시지 초기화
        joinBtn.disabled = false;
    }
}

email.addEventListener('input', validateForm);
userName.addEventListener('input', validateForm);
postCode.addEventListener('input', validateForm);
address1.addEventListener('input', validateForm);
address3.addEventListener('input', validateForm);