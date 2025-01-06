console.log("refund.js in");

document.addEventListener("DOMContentLoaded", () => {
    IMP.init("imp05450825");

    const cancelBtns = document.querySelectorAll(".cancelBtn");  // 모든 환불 버튼을 선택

    const onClickRefund = async (event) => {
        // 클릭한 버튼에서 결제 정보를 가져오기
        const button = event.target;
        const impUid = event.target.getAttribute("data-impUid");  // impUid
        const merchantUid = event.target.getAttribute("data-merchantUid");  // merchantUid
        const paidAmount = parseInt(event.target.getAttribute("data-paidAmount"));  // paidAmount
        const cancelFlag = button.getAttribute("data-cancelFlag");  // cancelFlag 값


        // cancelFlag가 1이면 "이미 환불 완료된 상품입니다" 메시지를 띄운다
          if (cancelFlag === true) {
            alert("이미 환불 완료된 상품입니다.");
            return;  // 환불 처리 중단
        }

        // 클릭한 버튼의 부모 요소에 있는 로딩 인디케이터 및 메시지 선택
        const buyProductElement = button.closest('.buyProduct');  // 해당 상품 요소
        const loadingIndicator = buyProductElement.querySelector('.loadingIndicator');  // 로딩 인디케이터
        const cancelMessage = buyProductElement.querySelector('.cancelMessage'); // 환불 처리 메시지

        // 로딩 인디케이터 표시
        loadingIndicator.style.display = 'block';
        cancelMessage.textContent = '환불 처리 중...';
        
        // 버튼 비활성화
        button.disabled = true;

        // 환불 요청을 백엔드로 전송
        fetch('/api/payment/refund', {
            method: 'POST',
            body: JSON.stringify({
                impUid: impUid,
                merchantUid: merchantUid,
                paidAmount: paidAmount
            }),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => response.json())
          .then(data => {
            if (data.status === "success") {
                // 환불 성공 후 리다이렉트
                window.location.href = data.redirectUrl;  // 서버에서 받은 리다이렉트 URL로 이동
            }
              console.log("환불 처리 결과", data);
          })
          .catch(error => {
              console.error("환불 처리 실패", error);
          })
          .finally(() => {
            // 환불 요청 완료 후 로딩 인디케이터 숨기기
            loadingIndicator.style.display = 'none';
            cancelMessage.textContent = '';
            button.disabled = false;  // 버튼 활성화
          });
    };

    // 모든 환불 버튼에 이벤트 리스너 추가
    cancelBtns.forEach(button => {
        button.addEventListener("click", onClickRefund);
    });
});










// console.log("refund.js in");

// document.addEventListener("DOMContentLoaded", () => {
//     IMP.init("imp05450825");

//     const cancelBtn = document.getElementById("cancelBtn");

//      const onClickRefund = async () => {
//             const impUid = "imp_747166742327";  // 실제 결제에 대한 impUid
//             const merchantUid = "BUY1733793166795-713";  // 실제 결제에 대한 merchantUid
//             const paidAmount = 100;  // 실제 결제 금액 (원래 결제된 금액)

//             // 환불 요청을 백엔드로 전송
//             fetch('/api/payment/refund', {
//                 method: 'POST',
//                 body: JSON.stringify({
//                     impUid: impUid,
//                     merchantUid: merchantUid,
//                     paidAmount: paidAmount
//                 }),
//                 headers: {
//                     'Content-Type': 'application/json'
//                 }
//             }).then(response => response.json())
//               .then(data => {
//                   console.log("환불 처리 결과", data);
//               })
//               .catch(error => {
//                   console.error("환불 처리 실패", error);
//               });
//         };

//     cancelBtn.addEventListener("click", onClickRefund);
// });
