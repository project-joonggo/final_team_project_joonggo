document.addEventListener('DOMContentLoaded', function () {
        const priceSearchForm = document.getElementById("priceSearchForm");
        const defaultSubTitle = document.querySelector(".product-subTitle.default");
        const hiddenBox = document.querySelector(".price-hiddenBox");
        const searchBox = document.querySelector(".price-searchBox");

        // 페이지 로드 시 상태 복원
        const isSearchActive = sessionStorage.getItem("isSearchActive");

        if (isSearchActive === "true") {
            defaultSubTitle.style.display = "none";
            hiddenBox.style.display = "block";
        } else {
            defaultSubTitle.style.display = "block";
            hiddenBox.style.display = "none";
        }

        // input창에서 Enter 키 입력을 감지하여 동작 처리
        priceSearchForm.addEventListener("submit", function(event) {
            // event.preventDefault(); // 기본 form 제출 동작을 막음

            // default 텍스트 숨기기
            if (defaultSubTitle) {
                defaultSubTitle.style.display = "none";
            }

            // hiddenBox 텍스트 보이기
            if (hiddenBox) {
                hiddenBox.style.display = "block";
            }

            // 폼 내용 처리 (이 부분은 실제로 폼을 제출할 때 사용할 수 있음)
            sessionStorage.setItem("isSearchActive", "true"); // 상태 저장
            priceSearchForm.submit(); // 실제로 서버로 폼을 제출하려면 이 줄을 사용
        });

        // Enter 키 누를 때마다 상태 저장
        searchBox.addEventListener("keydown", function(event) {
            if (event.key === "Enter") { // Enter 키가 눌렸을 때
                // event.preventDefault(); // Enter 키로 인한 폼 제출 방지

                // 가격 정보 박스 토글
                if (hiddenBox.style.display === "none") {
                    hiddenBox.style.display = "block";
                    defaultSubTitle.style.display = "none";
                } else {
                    hiddenBox.style.display = "none";
                    defaultSubTitle.style.display = "block";
                }

                // 상태 저장
                if (hiddenBox.style.display === "block") {
                    sessionStorage.setItem("isSearchActive", "true");
                } else {
                    sessionStorage.setItem("isSearchActive", "false");
                }
            }
        });
    });