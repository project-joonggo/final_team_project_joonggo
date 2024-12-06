console.log("join.js in~~~~");

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

        fetch(`/login/phoneCheck?phone=${pn}`, { method: "GET", cache: "no-cache" })
            .then((response) => response.text())
            .then((data) => {
                if (data === "error") {
                    alert("휴대폰 번호가 올바르지 않습니다.");
                } else {
                    checkInput.disabled = false;
                    dupCheck.textContent = "인증번호를 입력한 뒤 본인인증을 눌러주십시오.";
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
        dupCheck.textContent = "인증번호를 입력한 뒤 본인인증을 눌러주십시오.";
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