console.log("buy.js in");

document.addEventListener("DOMContentLoaded", () => {
    IMP.init("imp05450825");

    const button = document.querySelector("button");

    const onClickPay = async () => {
        IMP.request_pay({
            pg: "kakaopay",
            escrow : true,
            pay_method: "card",
            amount: 200,
            name: "라면2",
            merchant_uid: "BUY20241202-000003",
        }, (rsp) => {
            if (rsp.success) {
                console.log("결제 성공", rsp);
            } else {
                console.log("결제 실패", rsp);
            }
        });
    };

    button.addEventListener("click", onClickPay);
});