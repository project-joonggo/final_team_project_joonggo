let chatSocket = null;
let chatStompClient = null;
let currentRoomId = null;

// WebSocket 연결
function connectWebSocket() {
    try {
        chatSocket = new SockJS('/ws/chat');
        chatStompClient = Stomp.over(chatSocket);

        // debug 로그 비활성화
        chatStompClient.debug = null;

        chatStompClient.connect(
            {},  // 헤더 객체
            function(frame) {  // 성공 콜백
                console.log('Connected: ' + frame);
                if(currentRoomId) {
                    chatStompClient.subscribe(`/topic/chat/${currentRoomId}`, function(message) {
                        const receivedMessage = JSON.parse(message.body);
                        displayMessage(receivedMessage);

                        // 새 메시지 수신 시 읽지 않은 메시지 카운트 업데이트
                        if (receivedMessage.commentUserNum !== userNum) {
                            // CustomEvent 발생
                            const event = new CustomEvent('newChatMessage');
                            window.dispatchEvent(event);
                        }
                    });

                    // 입장 메시지 구독
                    chatStompClient.subscribe(`/topic/chat/${currentRoomId}/enter`, function(message) {
                        const enterInfo = JSON.parse(message.body);
                        console.log('User entered:', enterInfo);

                        // 상대방이 입장했을 때 자동 읽음 처리
                        if(enterInfo.userNum !== userNum) {
                            fetch(`/chat/read/${currentRoomId}?userNum=${userNum}`, {
                                method: 'POST'
                            }).then(() => {
                                // 읽음 처리 후 헤더 배지 업데이트
                                fetch(`/chat/unread/total?userNum=${userNum}`)
                                    .then(res => res.json())
                                    .then(count => {
                                        const headerBadge = document.getElementById('headerUnreadBadge');
                                        if (headerBadge) {
                                            headerBadge.textContent = count > 0 ? count : '';
                                            headerBadge.style.display = count > 0 ? 'inline' : 'none';
                                        }
                                    });
                            });
                        }
                    });
                    // 입장 메시지 전송
                    chatStompClient.send(`/app/chat/${currentRoomId}/enter`, {}, JSON.stringify({
                        roomId: currentRoomId,
                        userNum: userNum
                    }));
                }
            }
            ,function(error) {  // 에러 콜백
                console.error('STOMP connection error:', error);
                setTimeout(connectWebSocket, 3000); // 3초 후 재연결 시도
            }
        );
    } catch (error) {
        console.error('WebSocket connection error:', error);
    }
}

// 메시지 표시
function displayMessage(message) {

    console.log(message);

    const chatMessages = document.getElementById('chatMessages');
    const div = document.createElement('div');
    div.className = 'chat-message';

    if (message.commentUserNum === userNum) {
        div.className += ' sent-message';
    } else {
        div.className += ' received-message';
    }

    div.textContent = `${message.commentUserNum} : ${message.commentContent}`;
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 채팅방 목록 클릭 이벤트
document.getElementById('roomList').addEventListener('click', async function(event) {
    const roomListItem = event.target.closest('li');
    if (roomListItem) {
        const roomId = roomListItem.getAttribute('data-room-id');
        const roomName = roomListItem.textContent;

        try {
            // 기존 채팅방 입장 처리
            const response = await fetch(`/chat/enterRoom?roomId=${roomId}&userNum=${userNum}`);
            const commentList = await response.json();

            // 읽음 처리 요청
            await fetch(`/chat/read/${roomId}?userNum=${userNum}`, {
                method: 'POST'
            });

            // 읽음 처리 후 해당 방의 badge 업데이트
            const roomBadge = roomListItem.querySelector('.badge');
            if (roomBadge) {
                roomBadge.style.display = 'none';
                roomBadge.textContent = '';
            }

            // 전체 안읽은 메시지 수도 업데이트
            fetch(`/chat/unread/total?userNum=${userNum}`)
                .then(res => res.json())
                .then(count => {
                    const headerBadge = document.getElementById('headerUnreadBadge');
                    if (headerBadge) {
                        headerBadge.textContent = count > 0 ? count : '';
                        headerBadge.style.display = count > 0 ? 'inline' : 'none';
                    }
                });

            const chatMessages = document.getElementById('chatMessages');
            chatMessages.innerHTML = '';

            commentList.forEach(comment => {
                displayMessage(comment);
            });

            document.getElementById('chatRoom').style.display = 'block';
            document.getElementById('roomTitle').textContent = roomName;

            currentRoomId = parseInt(roomId);
            if (chatStompClient) {
                console.log('Disconnecting from previous room');
                chatStompClient.disconnect();
            }
            console.log('Connecting to new room(roomId):', roomId);
            console.log('Connecting to new room(currentRoomId):', currentRoomId);

            connectWebSocket();

        } catch (error) {
            console.error('Error:', error);
            alert('채팅방을 불러오는 중 오류가 발생했습니다.');
        }
    }
});

// 메시지 전송
document.getElementById('sendButton').addEventListener('click', () => {
    const messageInput = document.getElementById('messageInput');
    const messageContent = messageInput.value;
    if (!messageContent.trim()) return;

    const messageData = {
        roomId: currentRoomId,
        commentUserNum: userNum,
        commentContent: messageContent
    };

    console.log('Sending message:', messageData);

    if (chatStompClient && chatStompClient.connected) {
        console.log('StompClient status:', chatStompClient.connected);
        chatStompClient.send("/app/chat/sendMessage", {}, JSON.stringify(messageData));
        messageInput.value = '';
    } else {
        console.log('chatStompClient not connected, reconnecting...');
        connectWebSocket();
    }
});

// Enter 키로 메시지 전송
document.getElementById('messageInput').addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        document.getElementById('sendButton').click();
    }
});