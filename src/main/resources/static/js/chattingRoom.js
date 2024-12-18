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
                        });
                    }
                },
                function(error) {  // 에러 콜백
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
    const chatMessages = document.getElementById('chatMessages');
    const div = document.createElement('div');
    div.className = 'chat-message';

    if (message.commentUserNum === userNum) {
            div.className += ' sent-message';
    } else {
        div.className += ' received-message';
    }

    div.textContent = `${message.commentUserNum}: ${message.commentContent}`;
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
            const response = await fetch(`/chat/enterRoom?roomId=${roomId}&userNum=${userNum}`);
            const commentList = await response.json();

            const chatMessages = document.getElementById('chatMessages');
            chatMessages.innerHTML = '';

            commentList.forEach(comment => {
                displayMessage(comment);
            });

//            document.getElementById('chatRoomList').style.display = 'none';
            document.getElementById('chatRoom').style.display = 'block';
            document.getElementById('roomTitle').textContent = roomName;

            currentRoomId = parseInt(roomId);
            if (chatStompClient) {
                console.log('Disconnecting from previous room');
                chatStompClient.disconnect();
            }
            console.log('Connecting to new room:', roomId);
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
        console.log('StompClient status:', chatStompClient.connected);  // 로그 추가
        chatStompClient.send("/app/chat/sendMessage", {}, JSON.stringify(messageData));
        messageInput.value = '';
    } else {
              console.log('chatStompClient not connected, reconnecting...');  // 로그 추가
              connectWebSocket();  // 연결이 끊어진 경우 재연결 시도
    }
});

// Enter 키로 메시지 전송
document.getElementById('messageInput').addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        document.getElementById('sendButton').click();
    }
});