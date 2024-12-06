console.log("buy.js in");

document.addEventListener("DOMContentLoaded", () => {
    IMP.init("imp05450825");

    const button = document.querySelector("button");

    const onClickPay = async () => {

        const merchantUid = "BUY" + new Date().getTime() + "-" + Math.floor(Math.random() * 1000);
        const product = boardFileDTO;


        console.log(product);

        IMP.request_pay({
            pg: "kakaopay",
            pay_method: "card",
            amount: product.boardVO.tradePrice,
            name: product.boardVO.boardName,
            merchant_uid: merchantUid,
        }, (rsp) => {
            if (rsp.success) {
                console.log("결제 성공", rsp);
                verifyPayment(rsp);
                fetch('/api/payment/success', {
                                    method: 'POST',
                                    body: JSON.stringify({
                                        merchantUid: rsp.merchant_uid,
                                        amount: rsp.paid_amount,
                                        boardId: product.boardVO.boardId,  // 해당 상품 ID
                                        productName: product.boardVO.boardName
                                    }),
                                    headers: {
                                        'Content-Type': 'application/json'
                                    }
                                }).then(response => {
                                    return response.json();
                                }).then(data => {
                                    console.log("결제 정보 DB에 저장 완료", data);
                                }).catch(error => {
                                    console.error("결제 정보 DB 저장 실패", error);
                                });
            } else {
                console.log("결제 실패", rsp);
            }
        });
    };

    button.addEventListener("click", onClickPay);
});



const verifyPayment = async (rsp) => {
    try {
        const verificationResponse = await fetch('/api/payment/verify', {
            method: 'POST',
            body: JSON.stringify({
                imp_uid: rsp.imp_uid,           // 결제 고유 ID
                merchant_uid: rsp.merchant_uid,  // 주문 고유 ID
                amount: rsp.paid_amount         // 실제 결제된 금액
            }),
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const verificationData = await verificationResponse.json();

        if (verificationData.status === "success") {
            console.log("결제 검증 성공", verificationData);
        } else {
            console.log("결제 검증 실패", verificationData);
        }
    } catch (error) {
        console.error("결제 검증 중 에러 발생", error);
    }
};