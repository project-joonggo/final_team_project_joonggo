let socket = null;
let stompClient = null;
let currentRoomId = null;

// WebSocket 연결
function connectWebSocket() {
    socket = new SockJS('/ws/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe(`/topic/chat/${currentRoomId}`, function (message) {
            const receivedMessage = JSON.parse(message.body);
            displayMessage(receivedMessage);
        });
    });
}

// 메시지 표시
function displayMessage(message) {
    const chatMessages = document.getElementById('chatMessages');
    const div = document.createElement('div');
    div.className = 'chat-message';
    // 실질적으로 나오는 값 : 1 : test message
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

            document.getElementById('chatRoomList').style.display = 'none';
            document.getElementById('chatRoom').style.display = 'block';
            document.getElementById('roomTitle').textContent = roomName;

            currentRoomId = parseInt(roomId);
            if (stompClient) {
                stompClient.disconnect();
            }
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

    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chat/sendMessage", {}, JSON.stringify(messageData));
        messageInput.value = '';
    }
});

// Enter 키로 메시지 전송
document.getElementById('messageInput').addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        document.getElementById('sendButton').click();
    }
});