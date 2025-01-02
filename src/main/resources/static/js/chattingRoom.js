let chatSocket = null;
let chatStompClient = null;
let currentRoomId = null;
const MAX_RECONNECT_ATTEMPTS = 5;
let reconnectCount = 0;
let subscriptions = new Map();

// WebSocket 구독 처리
function subscribeToRoom() {
    if(currentRoomId) {
        // 기존 구독이 있다면 제거
        if(subscriptions.has(currentRoomId)) {
            subscriptions.get(currentRoomId).forEach(sub => sub.unsubscribe());
            subscriptions.delete(currentRoomId);
        }

        const subs = [];

        // 메시지 구독
        subs.push(chatStompClient.subscribe(`/topic/chat/${currentRoomId}`, function(message) {
            const receivedMessage = JSON.parse(message.body);
            displayMessage(receivedMessage);

            if (receivedMessage.commentUserNum !== userNum) {
                const event = new CustomEvent('newChatMessage');
                window.dispatchEvent(event);
            }
        }));

        // 입장 메시지 구독
        subs.push(chatStompClient.subscribe(`/topic/chat/${currentRoomId}/enter`, function(message) {
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
        subs.push(chatStompClient.subscribe(`/topic/chat/${currentRoomId}/leave`, function(message) {
            const leaveInfo = JSON.parse(message.body);
            console.log('User left:', leaveInfo);
        }));

        subscriptions.set(currentRoomId, subs);     // 웹소켓 구독정보를 저장하는 코드. 얘때문에 1로 저장되는거같음.

        // 입장 메시지 전송
        chatStompClient.send(`/app/chat/${currentRoomId}/enter`, {}, JSON.stringify({
            roomId: currentRoomId,
            userNum: userNum
        }));
    }
}

// WebSocket 연결
function connectWebSocket() {
    try {
        chatSocket = new SockJS('/ws/chat');
        chatStompClient = Stomp.over(chatSocket);
        chatStompClient.debug = null;

        return new Promise((resolve, reject) => {
            chatStompClient.connect(
                {},
                function(frame) {
                    console.log('Connected: ' + frame);
                    reconnectCount = 0;
                    subscribeToRoom();
                    resolve();
                },
                function(error) {
                    console.error('STOMP connection error:', error);
                    if (reconnectCount < MAX_RECONNECT_ATTEMPTS) {
                        setTimeout(() => {
                            reconnectCount++;
                            connectWebSocket();
                        }, 3000);
                    } else {
                        reject(error);
                    }
                }
            );
        });
    } catch (error) {
        console.error('WebSocket connection error:', error);
        return Promise.reject(error);
    }
}

// 채팅방 퇴장
function leaveChatRoom() {

    console.log('Attempting to leave chat room...');
    console.log('Current room ID:', currentRoomId);
    console.log('StompClient status:', chatStompClient ? 'exists' : 'null');


    if (chatStompClient && currentRoomId) {
        try {
            console.log('Sending leave message for room:', currentRoomId);

            // 1. 퇴장 메세지 전송.
            chatStompClient.send(`/app/chat/${currentRoomId}/leave`, {}, JSON.stringify({
                roomId: currentRoomId,
                userNum: userNum
            }));

            // 2. stomp 연결 해제 - 콜백에서 정리작업 수행
            chatStompClient.disconnect(() => {
                console.log('StompClient disconnected successfully');

                // 3. 구독 해제.
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

                // 4. 변수 초기화.
                chatStompClient = null;
                currentRoomId = null;
                console.log('Chat room left successfully');
            });
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
        .then(res => res.json())
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

// 채팅방 입장 이벤트
document.getElementById('roomList').addEventListener('click', async function(event) {
    const roomListItem = event.target.closest('li');
    if (roomListItem) {
        const roomId = roomListItem.getAttribute('data-room-id');
        const roomName = roomListItem.textContent;

        try {
            const inputContainer = document.querySelector('.message-input-container');
            const messageInput = document.getElementById('messageInput');
            const sendButton = document.getElementById('sendButton');

            inputContainer.classList.remove('disabled');
            messageInput.disabled = false;
            sendButton.disabled = false;
            messageInput.placeholder = 'Type your message...';

            const response = await fetch(`/chat/enterRoom?roomId=${roomId}&userNum=${userNum}`);
            const data = await response.json();

            await fetch(`/chat/read/${roomId}?userNum=${userNum}`, {
                method: 'POST'
            });

            const roomBadge = roomListItem.querySelector('.badge');
            if (roomBadge) {
                roomBadge.style.display = 'none';
                roomBadge.textContent = '';
            }

            updateHeaderBadge();

            const chatMessages = document.getElementById('chatMessages');
            chatMessages.innerHTML = '';

            data.comments.forEach(comment => {
                displayMessage(comment, data.otherUser);
            });

            document.getElementById('chatRoom').style.display = 'block';
            document.getElementById('roomTitle').textContent = roomName;

            if (currentRoomId) {
                leaveChatRoom();
            }

            currentRoomId = parseInt(roomId);
            await connectWebSocket();
            await checkRoomUserCount();  // 사용자 수 체크 추가
        } catch (error) {
            console.error('Error:', error);
            alert('채팅방을 불러오는 중 오류가 발생했습니다.');
        }
    }
});

// 메시지 전송
async function sendMessage(messageData) {
    if (!chatStompClient || !chatStompClient.connected) {
        try {
            await connectWebSocket();
        } catch (error) {
            console.error('Failed to reconnect:', error);
            alert('연결이 끊어졌습니다. 페이지를 새로고침해주세요.');
            return;
        }
    }

    try {
        chatStompClient.send("/app/chat/sendMessage", {}, JSON.stringify(messageData));
    } catch (error) {
        console.error('Failed to send message:', error);
        alert('메시지 전송에 실패했습니다.');
    }
}

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