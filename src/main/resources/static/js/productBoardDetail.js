console.log(" PBD.JS IN");

console.log(boardId);
console.log(tradeFlag);

// 게시글 수정 가능 여부 체크 함수
function checkEditable(tradeFlag, boardId) {
    if (tradeFlag === 1) {
        // 거래 완료된 게시글은 수정 불가능
        alert("거래 완료된 게시글은 수정이 불가능합니다.");
    } else {
        // 거래 완료되지 않은 게시글은 수정 페이지로 이동
        window.location.href = `/board/modify?boardId=${boardId}`;
    }
}

// 페이지가 로드된 후, 수정 버튼을 찾아 클릭 이벤트 처리
document.addEventListener("DOMContentLoaded", function() {
    // 수정 버튼을 찾아 클릭 이벤트 리스너 추가
    const modBtn = document.getElementById('modBtn');
    const wishBtn = document.getElementById('wishBtn');

    if (modBtn) {
        modBtn.addEventListener("click", function(event) {
            // 수정 가능 여부 체크
            checkEditable(tradeFlag, boardId);
            event.preventDefault();  // 기본 동작 방지
        });
    }

    if (wishBtn) {
        wishBtn.addEventListener("click", function() {
            // data-* 속성으로 boardId와 userNum을 가져옵니다.
            const boardId = wishBtn.getAttribute("data-board-id");
        

            console.log(boardId);

                // POST 요청으로 찜 추가 또는 취소
                fetch("/wish/getWish", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        boardId: boardId
                    })
                })
                .then(response => response.json())
                .then(data => {
                     // 찜 추가/취소 후 알림
                     alert(data.message);
                    
                     // 버튼 텍스트 변경 (예: 찜하기 <-> 찜 취소)
                     wishBtn.textContent = data.newButtonText;
                })
                .catch(error => console.error('Error:', error));
            });
        }

});