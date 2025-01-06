//let chatSocket = null;
//let chatStompClient = null;
let currentRoomId = null;
//const MAX_RECONNECT_ATTEMPTS = 5;
//let reconnectCount = 0;
let subscriptions = new Map();

// websocket 구독
function subscribeToRoom() {
   if(currentRoomId) {
       // 기존 구독이 있다면 제거
       if(subscriptions.has(currentRoomId)) {
           subscriptions.get(currentRoomId).forEach(sub => sub.unsubscribe());
           subscriptions.delete(currentRoomId);
       }

       const subs = [];

    // 메시지 구독
    subs.push(chatWebSocketManager.stompClient.subscribe(`/topic/chat/${currentRoomId}`, function(message) {
        const data = JSON.parse(message.body);
        console.log("Received message data:", data); // 데이터 확인
        const receivedMessage = data.message;
        const roomUnreadCount = data.roomUnreadCount;
        const totalUnreadCount = data.totalUnreadCount;

        // 메시지 표시
        fetch(`/chat/enterRoom?roomId=${currentRoomId}&userNum=${userNum}`)
            .then(response => response.json())
            .then(roomData => {
                displayMessage(receivedMessage, roomData.otherUser);

                // 받는 사람일 경우에만 배지 업데이트(발신자는 업데이트 X)

                if (receivedMessage.commentUserNum !== userNum) {
                    // 헤더 배지 업데이트
                    chatWebSocketManager.updateHeaderBadge(totalUnreadCount);

                    // 현재 채팅방 배지 업데이트
                    const currentRoomBadge = document.querySelector(`li[data-room-id="${currentRoomId}"] .badge`);
                    if (currentRoomBadge) {
                        if (roomUnreadCount > 0) {
                            currentRoomBadge.textContent = roomUnreadCount;
                            currentRoomBadge.style.display = 'inline';
                        } else {
                            currentRoomBadge.textContent = '';
                            currentRoomBadge.style.display = 'none';
                        }
                    }

                    fetch(`/chat/unread/rooms?userNum=${userNum}`)
                        .then(response => response.json())
                        .then(unreadCounts => {
                            // 모든 채팅방의 배지 업데이트
                            const roomListItems = document.querySelectorAll('.room-list-item');
                            roomListItems.forEach(roomItem => {
                                const roomId = roomItem.getAttribute('data-room-id');
                                const badge = roomItem.querySelector('.badge');
                                const count = unreadCounts[roomId];

                                // 현재 사용자가 수신자인 경우만 배지 표시
                                const roomNameSpan = roomItem.querySelector('span:not(.badge)');
                                if (badge && roomNameSpan) {
                                    badge.textContent = count > 0 ? count : '';
                                    badge.style.display = count > 0 ? 'inline' : 'none';
                                }
                            });
        //
                        })
                        .catch(error => console.error('Error updating room badges:', error));
                }
            });
    }));

       // 입장 메시지 구독
       subs.push(chatWebSocketManager.stompClient.subscribe(`/topic/chat/${currentRoomId}/enter`, function(message) {
           const enterInfo = JSON.parse(message.body);
           console.log('User entered:', enterInfo);

           if(enterInfo.userNum !== userNum) {
               fetch(`/chat/read/${currentRoomId}?userNum=${userNum}`, {
                   method: 'POST'
               }).then(() => {
                   updateHeaderBadge();
               });
           }
       }));

       // 퇴장 메시지 구독
       subs.push(chatWebSocketManager.stompClient.subscribe(`/topic/chat/${currentRoomId}/leave`, function(message) {
           const leaveInfo = JSON.parse(message.body);
           console.log('User left:', leaveInfo);
       }));

        subs.push(chatWebSocketManager.stompClient.subscribe(`/topic/chat/${currentRoomId}/unread`, function(message) {
            const unreadCount = JSON.parse(message.body);
            console.log('Received unread count update:', unreadCount);
            chatWebSocketManager.updateHeaderBadge(unreadCount);
        }));

       subscriptions.set(currentRoomId, subs);

       // 입장 메시지 전송
       chatWebSocketManager.stompClient.send(`/app/chat/${currentRoomId}/enter`, {}, JSON.stringify({
           roomId: currentRoomId,
           userNum: userNum
       }));
   }
}

// WebSocket 연결
async function connectWebSocket() {
    try {
        await chatWebSocketManager.connect(userNum);
        subscribeToRoom();
    } catch (error) {
        console.error('WebSocket connection error:', error);
    }
}

// 채팅방 퇴장
function leaveChatRoom() {
    console.log('Attempting to leave chat room...');
    console.log('Current room ID:', currentRoomId);

    if (chatWebSocketManager.stompClient && currentRoomId) {
        try {
            console.log('Sending leave message for room:', currentRoomId);

            // 퇴장 메세지 전송
            chatWebSocketManager.stompClient.send(`/app/chat/${currentRoomId}/leave`, {}, JSON.stringify({
                roomId: currentRoomId,
                userNum: userNum
            }));

            // 구독 해제
            if(subscriptions.has(currentRoomId)) {
                console.log('Unsubscribing from room:', currentRoomId);
                subscriptions.get(currentRoomId).forEach(sub => {
                    try {
                        sub.unsubscribe();
                        console.log('Subscription unsubscribed successfully');
                    } catch (e) {
                        console.error('Error unsubscribing:', e);
                    }
                });
                subscriptions.delete(currentRoomId);
                console.log('Subscriptions cleared for room:', currentRoomId);
            }

            // 변수 초기화
            currentRoomId = null;
            console.log('Chat room left successfully');

            document.getElementById('leaveRoomButton').style.display = 'none';
                document.querySelector('.message-input-container').style.display = 'none';
            document.getElementById('leaveRoomButton').style.display = 'none';

        } catch (error) {
            console.error('Error leaving chat room:', error);
        }
    } else {
        console.log('No active chatRoom or StompClient to leave');
    }
}

// 헤더 배지 업데이트
function updateHeaderBadge() {
    fetch(`/chat/unread/total?userNum=${userNum}`)
        .then(res => {
            console.log('Badge API response:', res);  // API 응답 로그
            return res.json();
        })
        .then(count => {
            const headerBadge = document.getElementById('headerUnreadBadge');
            if (headerBadge) {
                headerBadge.textContent = count > 0 ? count : '';
                headerBadge.style.display = count > 0 ? 'inline' : 'none';
            }
        })
        .catch(error => console.error('Error updating header badge:', error));
}

// 메시지 표시
function displayMessage(message, otherUser) {

    const chatMessages = document.getElementById('chatMessages');
    const div = document.createElement('div');
    div.className = 'chat-message';

    // 기존 메시지는 DB 날짜 사용, 새 메시지는 현재 날짜 사용
    const currentDate = new Date().toISOString().split('T')[0];
    const displayDate = message.commentWriteDate ? message.commentWriteDate.substring(0,10) : currentDate;

    if (message.commentUserNum === userNum) {
        div.className += ' sent-message';
        div.innerHTML = `
            <div class='chat-sender'>나</div>
            <div class='chat-content'>${message.commentContent}</div>
            <div class='chat-sender-date'>${displayDate}</div>
        `;
    } else {
        div.className += ' received-message';
        div.innerHTML = `
            <div class='chat-sender'>${otherUser.userName}<span class="sender-id"> (${otherUser.userId})</span></div>
            <div class='chat-content'>${message.commentContent}</div>
            <div class='chat-receiver-date'>${displayDate}</div>
        `;
    }

    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 메시지 전송
async function sendMessage(messageData) {
    if (!chatWebSocketManager.stompClient || !chatWebSocketManager.stompClient.connected) {
        try {
            await connectWebSocket();
        } catch (error) {
            console.error('Failed to reconnect:', error);
            alert('연결이 끊어졌습니다. 페이지를 새로고침해주세요.');
            return;
        }
    }

    try {
        chatWebSocketManager.stompClient.send("/app/chat/sendMessage", {}, JSON.stringify(messageData));
    } catch (error) {
        console.error('Failed to send message:', error);
        alert('메시지 전송에 실패했습니다.');
    }
}

// 채팅방 입장 이벤트
document.getElementById('roomList').addEventListener('click', async function(event) {
    const roomListItem = event.target.closest('li');
    if (roomListItem) {
        const roomId = roomListItem.getAttribute('data-room-id');
        const roomName = roomListItem.textContent;

        try {
            // 1. 이전 채팅방이 있다면 먼저 나가기
            if (currentRoomId) {
                await new Promise((resolve) => {
                    leaveChatRoom();
                    setTimeout(resolve, 500);
                });
            }

            // 2. 새 채팅방 정보 가져오기
            const response = await fetch(`/chat/enterRoom?roomId=${roomId}&userNum=${userNum}`);
            const data = await response.json();

            // 3. 채팅방 읽음 처리
            await fetch(`/chat/read/${roomId}?userNum=${userNum}`, {
                method: 'POST'
            });

            // 4. 채팅방 UI 초기화
            const chatMessages = document.getElementById('chatMessages');
            chatMessages.innerHTML = '';
            document.getElementById('chatRoom').style.display = 'block';
            document.getElementById('roomTitle').textContent = roomName;
            document.getElementById('leaveRoomButton').style.display = 'block';
            document.querySelector('.message-input-container').style.display = 'flex';

            // 5. 메시지 입력 UI 초기화
            const inputContainer = document.querySelector('.message-input-container');
            const messageInput = document.getElementById('messageInput');
            const sendButton = document.getElementById('sendButton');
            inputContainer.classList.remove('disabled');
            messageInput.disabled = false;
            sendButton.disabled = false;
            messageInput.placeholder = 'Type your message...';

            // 6. 채팅방 배지 업데이트
            const roomBadge = roomListItem.querySelector('.badge');
            if (roomBadge) {
                roomBadge.style.display = 'none';
                roomBadge.textContent = '';
            }
            chatWebSocketManager.fetchUnreadCount(userNum);

            // 7. 이전 메시지 표시
            data.comments.forEach(comment => {
                displayMessage(comment, data.otherUser);
            });

            // 8. 새 채팅방 연결 설정
            currentRoomId = parseInt(roomId);
            await connectWebSocket();
            await checkRoomUserCount();

        } catch (error) {
            console.error('Error:', error);
            alert('채팅방을 불러오는 중 오류가 발생했습니다.');
        }
    }
});

// 전송버튼 클릭 이벤트
document.getElementById('sendButton').addEventListener('click', async () => {
    const messageInput = document.getElementById('messageInput');
    const messageContent = messageInput.value.trim();
    if (!messageContent) return;

    const messageData = {
        roomId: currentRoomId,
        commentUserNum: userNum,
        commentContent: messageContent
    };

    await sendMessage(messageData);
    messageInput.value = '';
});

// Enter 키 이벤트
document.getElementById('messageInput').addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        document.getElementById('sendButton').click();
    }
});

// 페이지 이탈 시 채팅방 퇴장 처리
window.addEventListener('beforeunload', function() {
    leaveChatRoom();
});

// 채팅방 나가기 버튼 이벤트
document.getElementById('leaveRoomButton').addEventListener('click', async () => {
    if (confirm('채팅방을 나가시겠습니까?')) {
        try {
            const response = await fetch(`/chat/room/leave?roomId=${currentRoomId}&userNum=${userNum}`, {
                method: 'POST'
            });

            if (response.ok) {
                window.location.href = `/chat/chatRoomList`;
            } else {
                throw new Error('채팅방 나가기 실패');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('채팅방 나가기에 실패했습니다.');
        }
    }
});

// 페이지 내의 모든 링크 클릭을 감지
document.body.addEventListener('click', async function(e) {
    // 현재 URL과 다른 페이지로 이동하는 a 태그인 경우에만 처리
    const clickedLink = e.target.closest('a');
    if (clickedLink &&
        clickedLink.href &&
        !clickedLink.href.includes('/chat/') &&  // 채팅 관련 페이지는 제외
        currentRoomId) {  // 현재 채팅방이 있는 경우에만

        e.preventDefault();  // 기본 이벤트 중지

        // 채팅방 나가기 처리
        await new Promise((resolve) => {
            leaveChatRoom();
            setTimeout(resolve, 500);
        });

        // 원래 이동하려던 페이지로 이동
        window.location.href = clickedLink.href;
    }
});

// form submit 이벤트도 처리
document.body.addEventListener('submit', async function(e) {
    if (currentRoomId) {
        e.preventDefault();
        await new Promise((resolve) => {
            leaveChatRoom();
            setTimeout(resolve, 500);
        });
        e.target.submit();
    }
});

// 채팅방 사용자 수 체크 및 입력창 비활성화 처리
async function checkRoomUserCount() {
    try {
        const response = await fetch(`/chat/room/userCount/${currentRoomId}`);
        const count = await response.json();

        const inputContainer = document.querySelector('.message-input-container');
        const messageInput = document.getElementById('messageInput');
        const sendButton = document.getElementById('sendButton');

        console.log(count);

        if (count === 1) {
            inputContainer.classList.add('disabled');
            messageInput.disabled = true;
            sendButton.disabled = true;
            messageInput.placeholder = '상대방이 채팅방을 나가 채팅이 종료되었습니다.';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}