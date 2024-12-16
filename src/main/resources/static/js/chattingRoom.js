//const userNum = 1; // Example: User ID
//let currentRoomId = null;
//
//// Fetch chat room list
//async function fetchChatRoomList() {
//    const response = await fetch(`/chat/roomList?userNum=${userNum}`);
//    const rooms = await response.json();
//    const roomList = document.getElementById('roomList');
//    roomList.innerHTML = '';
//
//    rooms.forEach(room => {
//        const li = document.createElement('li');
//        li.textContent = room.roomName;
//        li.style.cursor = 'pointer';
//        li.addEventListener('click', () => enterChatRoom(room.roomId, room.roomName));
//        roomList.appendChild(li);
//    });
//}
//
//// Enter chat room
//async function enterChatRoom(roomId, roomName) {
//    currentRoomId = roomId;
//    document.getElementById('chatRoomList').style.display = 'none';
//    document.getElementById('chatRoom').style.display = 'block';
//    document.getElementById('roomTitle').textContent = roomName;
//
//    const response = await fetch(`/chat/enterRoom?roomId=${roomId}&userNum=${userNum}`);
//    const comments = await response.json();
//    const chatMessages = document.getElementById('chatMessages');
//    chatMessages.innerHTML = '';
//
//    comments.forEach(comment => {
//        const div = document.createElement('div');
//        div.className = 'chat-message';
//        div.textContent = `${comment.commentUserNum}: ${comment.commentContent}`;
//        chatMessages.appendChild(div);
//    });
//}
//
//// Send message
//document.getElementById('sendButton').addEventListener('click', async () => {
//    const messageInput = document.getElementById('messageInput');
//    const messageContent = messageInput.value;
//    if (!messageContent.trim()) return;
//
//    await fetch('/chat/sendMessage', {
//        method: 'POST',
//        headers: {
//            'Content-Type': 'application/x-www-form-urlencoded',
//        },
//        body: `roomId=${currentRoomId}&userNum=${userNum}&messageContent=${messageContent}`,
//    });
//
//    messageInput.value = '';
//    await enterChatRoom(currentRoomId, document.getElementById('roomTitle').textContent);
//});
//
//// Initial fetch
//fetchChatRoomList();

let socket = null;
let stompClient = null;

function connectWebSocket() {
    // SockJS와 STOMP를 사용한 WebSocket 연결
    socket = new SockJS('/ws-chat'); // 백엔드 WebSocket 엔드포인트
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // 특정 채팅방의 메시지 구독
        stompClient.subscribe(`/topic/chat/${currentRoomId}`, function (message) {
            const receivedMessage = JSON.parse(message.body);
            displayMessage(receivedMessage);
        });
    });
}

// 메시지 표시 함수
function displayMessage(message) {
    const chatMessages = document.getElementById('chatMessages');
    const div = document.createElement('div');
    div.className = 'chat-message';
    div.textContent = `${message.commentUserNum}: ${message.commentContent}`;
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight; // 최신 메시지로 스크롤
}

// 메시지 전송 수정
document.getElementById('sendButton').addEventListener('click', async () => {
    const messageInput = document.getElementById('messageInput');
    const messageContent = messageInput.value;
    if (!messageContent.trim()) return;

    // WebSocket을 통해 메시지 전송
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chat/sendMessage", {}, JSON.stringify({
            roomId: currentRoomId,
            userNum: userNum,
            messageContent: messageContent
        }));

        messageInput.value = '';
    }
});

// 채팅방 입장 시 WebSocket 연결
async function enterChatRoom(roomId, roomName) {
    currentRoomId = roomId;
    document.getElementById('chatRoomList').style.display = 'none';
    document.getElementById('chatRoom').style.display = 'block';
    document.getElementById('roomTitle').textContent = roomName;

    // 기존 메시지 불러오기
    const response = await fetch(`/chat/enterRoom?roomId=${roomId}&userNum=${userNum}`);
    const comments = await response.json();
    const chatMessages = document.getElementById('chatMessages');
    chatMessages.innerHTML = '';

    comments.forEach(comment => {
        const div = document.createElement('div');
        div.className = 'chat-message';
        div.textContent = `${comment.commentUserNum}: ${comment.commentContent}`;
        chatMessages.appendChild(div);
    });

    // WebSocket 연결 (이전 연결 닫기 후 새로 연결)
    if (stompClient) {
        stompClient.disconnect();
    }
    connectWebSocket();
}

// 초기 로드 시
fetchChatRoomList();