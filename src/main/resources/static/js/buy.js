console.log("buy.js in");

document.addEventListener("DOMContentLoaded", () => {
    IMP.init("imp05450825");

    const paymentBtn = document.getElementById("paymentBtn");

    const onClickPay = async () => {

        const merchantUid = "BUY" + new Date().getTime() + "-" + Math.floor(Math.random() * 1000);
        const product = boardFileDTO;

        console.log(product);

        if (product.boardVO.tradeFlag === 1) {
            alert("이미 거래가 완료된 게시글입니다. 결제가 불가능합니다.");
            paymentBtn.disabled = true;  // 결제 버튼 비활성화
            return;  // 결제 로직을 더 이상 진행하지 않음
        }


        IMP.request_pay({
            pg: "html5_inicis",
            pay_method: "card",
            amount: product.boardVO.tradePrice,
            name: product.boardVO.boardName,
            merchant_uid: merchantUid,
            notice_url: "https://be8b-112-220-50-35.ngrok-free.app/api/payment/webhook"
        }, (rsp) => {
            if (rsp.success) {
                console.log("결제 성공", rsp);
//                verifyPayment(rsp);
                fetch('/api/payment/success', {
                                    method: 'POST',
                                    body: JSON.stringify({
                                        merchantUid: rsp.merchant_uid,
                                        amount: rsp.paid_amount,
                                        boardId: product.boardVO.boardId,  // 해당 상품 ID
                                        productName: product.boardVO.boardName,
                                        impUid : rsp.imp_uid
                                    }),
                                    headers: {
                                        'Content-Type': 'application/json'
                                    }
                                }).then(response => {
                                    return response.json();
                                }).then(data => {
                                    if (data.status === "success") {
                                        // 응답에서 리다이렉트 URL을 받아서 페이지 이동
                                        window.location.href = data.redirectUrl;  // 리다이렉트 URL로 이동
                                    } 
                                    console.log("결제 정보 DB에 저장 완료", data);
                                }).catch(error => {
                                    console.error("결제 정보 DB 저장 실패", error);
                                });
            } else {
                console.log("결제 실패", rsp);
            }
        });
    };


    paymentBtn.addEventListener("click", onClickPay);

});
