console.log("refund.js in");

document.addEventListener("DOMContentLoaded", () => {
    const cancelBtn = document.getElementById("cancelBtn"); // 환불 버튼

    cancelBtn.addEventListener("click", async () => {
        const impUid = "BUY1733706451499-673";  // 예시로 결제 고유 ID
        const merchantUid = "imp_962453126823";  // 예시로 주문 고유 ID
        const cancelAmount = 100;  // 환불할 금액. 전체 금액 또는 일부 금액
        const product = boardFileDTO;  // 환불 요청 시 상품 정보

        try {
            const cancelResponse = await fetch('/api/payment/cancel', {
                method: 'POST',
                body: JSON.stringify({
                    imp_uid: impUid,
                    merchant_uid: merchantUid,
                    amount: cancelAmount,
                    reason: "사기꾼!!!!!!!!!",  // 환불 사유
                    productName: product.boardVO.boardName  // 상품 이름 추가
                }),
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const cancelData = await cancelResponse.json();

            console.log(cancelResponse);

            if (cancelData.status === "success") {
                console.log("결제 취소 성공", cancelData);
                alert("결제가 취소되었습니다.");
            } else {
                console.error("결제 취소 실패", cancelData);
                alert("결제 취소에 실패했습니다.");
            }
        } catch (error) {
            console.error("환불 요청 중 에러 발생", error);
            alert("환불 요청 중 오류가 발생했습니다.");
        }
    });
});