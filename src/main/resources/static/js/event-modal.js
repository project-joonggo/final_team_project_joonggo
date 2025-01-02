    // 모달 DOM 요소
    const customAlert = document.getElementById("custom-alert");
    const alertMessage = document.getElementById("alert-message");
    const alertOkButton = document.getElementById("alert-ok-button");

    // 커스터마이즈된 alert 함수
    const showAlert = (message) => {
        alertMessage.textContent = message;
        customAlert.classList.remove("hidden");
        customAlert.style.display = "block";
    };

    // "확인" 버튼 클릭 시 모달 닫기
    alertOkButton.addEventListener("click", () => {
        customAlert.classList.add("hidden");
        customAlert.style.display = "none";
    });