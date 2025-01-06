console.log("wishbuy.js in");

document.addEventListener("DOMContentLoaded", () => {
    // IMP 결제 연동 초기화
    IMP.init("imp05450825");


    document.querySelectorAll('.pay-btn').forEach(button => {
        button.addEventListener('click', function() {
            const boardId = button.getAttribute('data-board-id');  // data-board-id에서 boardId 가져오기
            console.log(boardId);
            openPaymentModal(boardId);  // 모달 열기
        });
    });

    // 결제 버튼 클릭 시 결제 처리
    document.querySelectorAll('.paymentBtn').forEach(button => {
        button.addEventListener('click', function() {
            const boardId = button.getAttribute('data-board-id');  // data-board-id에서 boardId 가져오기
            console.log(boardId);
            onPaymentBtnClick(boardId);  // 결제
        });
    });

    // 결제 함수
    function onPaymentBtnClick(boardId) {
        console.log("결제하기 버튼 클릭! boardId:", boardId);

        // boardId를 기반으로 결제 처리
        const merchantUid = "BUY" + new Date().getTime() + "-" + Math.floor(Math.random() * 1000);
        const product = boardFileDTO;

        // 해당 boardId를 가진 상품 찾기
        const selectedProduct = product.find(item => item.boardVO.boardId === parseInt(boardId));

        if (!selectedProduct) {
            console.log("해당 게시글을 찾을 수 없습니다.");
            return;
        }

        const boardVO = selectedProduct.boardVO;

        // 거래 상태 확인 (tradeFlag가 1이면 결제 불가능)
        if (boardVO.tradeFlag === 1) {
            alert("이미 거래가 완료된 게시글입니다. 결제가 불가능합니다.");
            return;  // 결제 로직을 더 이상 진행하지 않음
        }

        // IMP 결제 요청
        IMP.request_pay({
            pg: "html5_inicis",  // PG사 설정
            pay_method: "card",  // 결제 방법
            amount: boardVO.tradePrice,  // 결제 금액
            name: boardVO.boardName,  // 상품명
            merchant_uid: merchantUid,  // 거래 고유 ID
            notice_url: "https://be8b-112-220-50-35.ngrok-free.app/api/payment/webhook"  // 결제 결과 알림 URL
        }, (rsp) => {
            if (rsp.success) {
                console.log("결제 성공", rsp);

                // 결제 성공 후 서버에 결제 정보 전송
                fetch('/api/payment/success', {
                    method: 'POST',
                    body: JSON.stringify({
                        merchantUid: rsp.merchant_uid,
                        amount: rsp.paid_amount,
                        boardId: boardVO.boardId,  // 해당 상품 ID
                        productName: boardVO.boardName,
                        impUid: rsp.imp_uid  // 결제 고유 ID
                    }),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => response.json())
                .then(data => {
                    if (data.status === "success") {
                        // 응답에서 리다이렉트 URL을 받아서 페이지 이동
                        window.location.href = data.redirectUrl;  // 리다이렉트 URL로 이동
                    }
                    console.log("결제 정보 DB에 저장 완료", data);
                })
                .catch(error => {
                    console.error("결제 정보 DB 저장 실패", error);
                });

            } else {
                console.log("결제 실패", rsp);
            }
        });
    }


    document.querySelectorAll('.close').forEach(button => {
        button.addEventListener('click', function() {
            const boardId = button.getAttribute('data-board-id');  // data-board-id에서 boardId 가져오기
            console.log(boardId);
            closePaymentModal(boardId);  // 모달 닫기
        });
    });

    // 모달 열기
    function openPaymentModal(boardId) {
        const modal = document.getElementById(`paymentModal_${boardId}`);
        if (modal) {
            if (principal === 'anonymousUser') {
               window.location.href = '/user/login';
             } else {
                modal.style.display = "block";  // 해당 모달 열기
             }
        }
    }

    // 모달 닫기
    function closePaymentModal(boardId) {
        const modal = document.getElementById(`paymentModal_${boardId}`);
        if (modal) {
            modal.style.display = "none";  // 해당 모달 닫기
        }
    }

});
