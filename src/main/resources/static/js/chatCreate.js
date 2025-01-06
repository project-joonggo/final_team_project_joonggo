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

                if (response.ok) {
                   if (data.status === 'success') {
                       if (data.isExisting) {
                           alert('기존 채팅방이 존재합니다. 채팅방으로 이동합니다.');
                       }
                       window.location.href = data.redirectUrl;
                   } else {
                       throw new Error(data.message || '채팅방 생성에 실패했습니다.');
                   }
               } else {
                   throw new Error('서버 응답 오류');
               }

            } catch (error) {
                console.error('Error details:', error);  // 에러 상세 로깅
                alert('로그인 후 이용해주세요.');
            }
        });
    });
});