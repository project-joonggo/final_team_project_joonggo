document.addEventListener('DOMContentLoaded', function() {
   // headerBadge 업데이트 함수 정의
   function updateHeaderBadge(count) {
       const headerBadge = document.getElementById('headerUnreadBadge');
       // console.log('headerBadge element:', headerBadge);
       if (headerBadge) {
           headerBadge.textContent = count > 0 ? count : '';
           headerBadge.style.display = count > 0 ? 'inline' : 'none';
           console.log('Badge updated with count:', count);
       }
   }

const chatLink = document.querySelector('a[data-user-num]');
// console.log('chatLink:', chatLink);

   if (chatLink) {
       const userNum = chatLink.dataset.userNum;
       console.log('Fetching unread count for userNum:', userNum);

       fetch(`/chat/unread/total?userNum=${userNum}`)
           .then(res => {
               // console.log('Response status:', res.status);
               if (!res.ok) throw new Error('Network response was not ok');
               return res.json();
           })
           .then(count => {
               // console.log('Received count:', count);
               updateHeaderBadge(count);
           })
           .catch(error => {
                console.error('Error : ', error);
                console.error('Error stack : ', error.stack);
           });
   } else {
        console.log('Chat link not found');
   }

window.addEventListener('newChatMessage', function() {
    console.log('New chat message event received');

   const chatLink = document.querySelector('a[data-user-num]');
   if (!chatLink) return;
   const userNum = chatLink.dataset.userNum;

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
//   if (typeof totalUnreadCount !== 'undefined') {
//       updateHeaderBadge(totalUnreadCount);
//   }
});