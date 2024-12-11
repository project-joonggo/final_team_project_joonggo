console.log("refund.js in");

document.addEventListener("DOMContentLoaded", () => {
    IMP.init("imp05450825");

    const cancelBtns = document.querySelectorAll(".cancelBtn");  // 모든 환불 버튼을 선택

    const onClickRefund = async (event) => {
        // 클릭한 버튼에서 결제 정보를 가져오기
        const impUid = event.target.getAttribute("data-impUid");  // impUid
        const merchantUid = event.target.getAttribute("data-merchantUid");  // merchantUid
        const paidAmount = parseInt(event.target.getAttribute("data-paidAmount"));  // paidAmount

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
              console.log("환불 처리 결과", data);
          })
          .catch(error => {
              console.error("환불 처리 실패", error);
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
