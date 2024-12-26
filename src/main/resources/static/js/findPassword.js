console.log("findPassword.js in");
const email = document.getElementById('i');
const finalMessage = document.getElementById("finalMessage");
const joinBtn = document.querySelector('.joinPostBtn');
let passwordFinalCheck = false;

const passwordInput = document.getElementById('password');
const passwordCheckInput = document.getElementById('passwordCheck');
const passwordCheckMessage = document.getElementById('passwordCheckMessage');
const passwordMessage = document.getElementById('passwordMessage');

///////////이메일 인증 line////////////////
// 이메일 인증 버튼 클릭 이벤트
document.getElementById("emailChk").addEventListener("click", function () {
    const emailInput = document.getElementById("i");
    const checkInput = document.getElementById("checkEmail");
    const dupCheck = document.getElementById("emailCheckMessage");

    const email = emailInput.value;

    alert("인증번호 발송이 완료되었습니다.\n이메일에서 인증번호를 확인해 주세요.");

    // 이메일 인증번호 발송 요청
    fetch(`/user/mailSend?email=${email}`, { method: "POST", cache: "no-cache" })
        .then((response) => response.text())
        .then((data) => {
            if (data === "error") {
                alert("이메일 발송에 실패했습니다. 다시 시도해주세요.");
                emailInput.readOnly = false;
            } else {
                // 이메일 발송 성공 후
                checkInput.disabled = false;  // 인증번호 입력칸 활성화
                dupCheck.textContent = "인증번호를 입력한 뒤 인증하기를 눌러주세요.";  // 안내 메시지 표시
                dupCheck.style.color = "red";  // 안내 메시지 색상 변경
                emailInput.readOnly = true;  // 이메일 입력칸은 더 이상 수정 불가
                authEmailCode = data;  // 인증번호를 저장
            }
        })
        .catch((error) => {
            console.error("Error:", error);
            emailInput.readOnly = false;
        });
    // 이메일 인증번호 확인 버튼 클릭 이벤트
    document.getElementById("emailChk2").addEventListener("click", function () {
        const checkInput = document.getElementById("checkEmail");
        const dupCheck = document.getElementById("emailCheckMessage");

        if (authEmailCode.length <= 0) {
            checkInput.disabled = false;
            dupCheck.textContent = "인증번호를 입력한 뒤 인증하기를 눌러주세요.";
            dupCheck.style.color = "red";
        } else if (checkInput.value === authEmailCode) {
            dupCheck.textContent = "인증번호가 일치합니다.";
            dupCheck.style.color = "green";
            checkInput.disabled = true;
            emailCheckNum = true;
        } else {
            dupCheck.textContent = "인증번호가 일치하지 않습니다. 다시 확인해주세요.";
            dupCheck.style.color = "red";
            checkInput.focus();
            emailCheckNum = false;
            return;
        }
    });

    // 회원가입 필드
    function validateForm() {
        if (email.value === '') {
            finalMessage.textContent = "아이디(이메일)을 입력해 주세요.";
            joinBtn.disabled = true; // 비활성화
            return;
        }

        // 모든 조건 충족
        finalMessage.textContent = ''; // 오류 메시지 초기화
        joinBtn.disabled = false; // 버튼 활성화
    }

    email.addEventListener('input', validateForm);
});

///////////////핸드폰 인증 line
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

// 핸드폰 인증번호 확인 버튼 클릭 이벤트
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

function validateForm() {
    if (email.value === '') {
        finalMessage.textContent = "아이디(이메일)을 인증해 주세요.";
        joinBtn.disabled = true; // 비활성화
        return;
    }
    if (!checkNum) {
        finalMessage.textContent = "휴대폰 번호를 인증해 주세요.";
        joinBtn.disabled = true;
        return;
    }
    if (!passwordFinalCheck) {
        finalMessage.textContent = "비밀번호를 입력해 주세요.";
        joinBtn.disabled = true;
        return;
    }

    // 모든 조건 충족
    finalMessage.textContent = ''; // 오류 메시지 초기화
    joinBtn.disabled = false; // 버튼 활성화
}

email.addEventListener('input', validateForm);
passwordInput.addEventListener('input', validateForm);
passwordCheckInput.addEventListener('input', validateForm);
document.getElementById('phoneChk2').addEventListener('click', validateForm);

