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

            if (principal === 'anonymousUser') {
                // 로그인하지 않은 경우, 로그인 페이지로 리다이렉트
                window.location.href = '/user/login';
            } else{
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
                        
                        // 서버 응답에 따라 아이콘 변경
                        if (data.newButtonText === '찜하기') {
                            // 찜 취소된 경우, 빈 하트 아이콘으로 변경
                            wishIcon.innerHTML = `
                                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-heart" viewBox="0 0 16 16">
                                    <path d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15"/>
                                </svg>
                            `;
                        } else if (data.newButtonText === '찜 취소') {
                            // 찜한 경우, 채워진 하트 아이콘으로 변경
                            wishIcon.innerHTML = `
                                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="red" class="bi bi-heart-fill" viewBox="0 0 16 16">
                                    <path fill-rule="evenodd" d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314"/>
                                </svg>
                            `;
                        }
                        // 찜 수 업데이트 (UI 반영)
                        const likeCountSpan = document.getElementById('likeCount');
                        if (likeCountSpan) {
                            likeCountSpan.textContent = data.newLikeCount;  // 새로운 찜 수 업데이트
                        }
                    })
                    .catch(error => console.error('Error:', error));
                }
            });
        }

});

document.addEventListener("DOMContentLoaded", function() {
    // boardContent div 내의 이미지 태그를 모두 삭제
    var boardContent = document.getElementById('boardContent');
    var images = boardContent.getElementsByTagName('img');
    while (images.length > 0) {
        images[0].parentNode.removeChild(images[0]);
    }
});

// 계산된 퍼센트를 게이지 바에 반영
var score = parseFloat(document.getElementById("score-value").textContent);
var percentage = (score / 100) * 100;
document.getElementById("score-bar").style.width = percentage + "%";



document.getElementById('toggleGraphButton').addEventListener('click', function() {
    var graphContainer = document.getElementById('priceGraph');
    var toggleIcon = document.getElementById('toggleIcon');
    // 현재 그래프가 보이는 상태면 숨기고, 숨겨져 있으면 보이게
    if (graphContainer.style.display === 'none') {
        graphContainer.style.display = 'block';
        // 아이콘을 위쪽 화살표로 변경 (innerHTML을 이용하여 변경)
        toggleIcon.innerHTML = `
            <path fill-rule="evenodd" d="M7.646 4.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1-.708.708L8 5.707l-5.646 5.647a.5.5 0 0 1-.708-.708z"/>
        `;
    } else {
        graphContainer.style.display = 'none';
        // 아이콘을 아래쪽 화살표로 변경 (innerHTML을 이용하여 변경)
        toggleIcon.innerHTML = `
            <path fill-rule="evenodd" d="M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708"/>
        `;
    }
});