document.addEventListener('DOMContentLoaded', function() {
   // headerBadge 업데이트 함수 정의
   function updateHeaderBadge(count) {
       const headerBadge = document.getElementById('headerUnreadBadge');
       if (headerBadge) {
           headerBadge.textContent = count > 0 ? count : '';
           headerBadge.style.display = count > 0 ? 'inline' : 'none';
           console.log('Badge updated with count:', count);
       }
   }

    const chatLink = document.querySelector('a[data-user-num]');

   if (chatLink) {
       const userNum = chatLink.dataset.userNum;

       fetch(`/chat/unread/total?userNum=${userNum}`)
           .then(res => {
               if (!res.ok) throw new Error('Network response was not ok');
               return res.json();
           })
           .then(count => {
               updateHeaderBadge(count);
           })
           .catch(error => {
                console.error('Error : ', error);
                console.error('Error stack : ', error.stack);
           });

        if (chatStompClient && chatStompClient.connected) {
                    // 사용자별 실시간 알림 구독
            chatStompClient.subscribe(`/topic/user/${userNum}/unread`, function(message) {
                const unreadCount = JSON.parse(message.body);
                updateHeaderBadge(unreadCount);
            });
        }
   }

// 새 메시지 이벤트 리스너
    window.addEventListener('newChatMessage', function(event) {
        console.log('New chat message event received');
        const userNum = event.detail.userNum;
        console.log('Updating badge for userNum:', userNum);

        fetch(`/chat/unread/total?userNum=${userNum}`)
            .then(res => {
                if (!res.ok) throw new Error('Network response was not ok');
                return res.json();
            })
            .then(count => {
                updateHeaderBadge(count);
            })
            .catch(error => console.error('Error:', error));
    });

   // 초기 로드 시 전체 안 읽은 메시지 수가 있다면 배지 업데이트
   if (typeof totalUnreadCount !== 'undefined') {
       updateHeaderBadge(totalUnreadCount);
   }
});