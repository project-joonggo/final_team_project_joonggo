document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.chat-btn').forEach(button => {
        button.addEventListener('click', async function() {
            const sellerId = this.getAttribute('data-seller-id');
            const boardName = this.getAttribute('data-board-name');
            const userNum = this.getAttribute('data-user-num');

            try {
                console.log('Sending request with data:', { sellerId, boardName, userNum });  // 요청 데이터 로깅

                const response = await fetch('/chat/room/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        userNum: parseInt(userNum),
                        sellerId: parseInt(sellerId),
                        roomName: boardName
                    })
                });

                const data = await response.json();
                console.log('Received response:', data);  // 응답 데이터 로깅

                if (response.ok && data.status === 'success') {
                    window.location.href = data.redirectUrl;
                } else {
                    throw new Error(data.message || '채팅방 생성에 실패했습니다.');
                }

            } catch (error) {
                console.error('Error details:', error);  // 에러 상세 로깅
                alert('채팅방 생성 중 오류가 발생했습니다.');
            }
        });
    });
});