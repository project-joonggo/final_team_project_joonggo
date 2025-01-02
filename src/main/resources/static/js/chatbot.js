// STOMP 클라이언트 변수
let chatbotStompClient = null;

document.addEventListener('DOMContentLoaded', function() {
    console.log("Chatbot script loaded");

    // DOM 요소 찾기
    const openChatBtn = document.getElementById("openChatBtn");
    const closeChatBtn = document.getElementById("closeChatBtn");
    const chatSidebar = document.getElementById("chatSidebar");
    const sendBtn = document.getElementById("sendBtn");
    const chatInput = document.getElementById("chatInput");
    const chatMessages = document.querySelector(".chat-messages");

    // STOMP 연결 함수
    function connectStomp() {
        console.log("Connecting to STOMP...");
        const chatbotSocket = new SockJS('/ws/chatbot');
        chatbotStompClient = Stomp.over(chatbotSocket);

        // STOMP 디버그 로그 비활성화
        chatbotStompClient.debug = null;

        chatbotStompClient.connect({}, function(frame) {
            console.log('Connected to STOMP:', frame);

            // 챗봇 응답 구독
            chatbotStompClient.subscribe('/topic/chatbot', function(response) {
                console.log('Received response:', response);
                const message = JSON.parse(response.body);
                addMessage('챗봇', message.content, 'bot');
            });

            // 연결 성공 메시지 표시
            addMessage('시스템', '챗봇과 대화를 시작하세요.', 'system');
        }, function(error) {
            console.error('STOMP connection error:', error);
            addMessage('시스템', '연결에 실패했습니다. 잠시 후 다시 시도해주세요.', 'system');
        });
    }

    // 메시지 표시 함수
    function addMessage(sender, message, type) {
        const messageElement = document.createElement("div");
        messageElement.classList.add("message", `message-${type}`);

        // 현재 시간 포맷팅
        const now = new Date();
        const timeString = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;

        // 줄바꿈 처리
        const formattedMessage = message.replace(/\n/g, '<br>');

        // type이 'system'이 아닐 경우에만 sender와 time을 표시
        if (type !== 'system') {
            messageElement.innerHTML = `
                <div class="message-sender">${sender}</div>
                <div class="message-content">${formattedMessage}</div>
                <div class="message-time">${timeString}</div>
            `;
        } else {
            messageElement.innerHTML = `
                <div class="message-content">${formattedMessage}</div>
            `;
        }

        chatMessages.appendChild(messageElement);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    // 메시지 전송 함수
    function sendMessage() {
        const message = chatInput.value.trim();
        if (message !== "" && chatbotStompClient && chatbotStompClient.connected) {
//            console.log('Sending message:', message);

            const userAddress = document.getElementById('userAddress')?.value || null;

            const messageData = {
                message: message,
                timestamp: new Date().toISOString(),
                userAddress: userAddress
            }
            console.log('Sending data : ', messageData);
            // 사용자 메시지 표시
            addMessage('사용자', message, 'user');

            console.log(userAddress);
            // 서버로 메시지 전송
            chatbotStompClient.send("/app/chatbot", {}, JSON.stringify(messageData));

            chatInput.value = "";
            chatInput.focus();

        } else if (!chatbotStompClient || !chatbotStompClient.connected) {
            console.log('Reconnecting STOMP...');
            connectStomp();
        }
    }

    // 대화방 열기 버튼 클릭 이벤트
    if (openChatBtn) {
        openChatBtn.onclick = function() {
            console.log("Opening chat sidebar");
            chatSidebar.classList.add("active");
            if (!chatbotStompClient || !chatbotStompClient.connected) {
                connectStomp();
            }
        }
    }

    // 대화방 닫기 버튼 클릭 이벤트
    if (closeChatBtn) {
        closeChatBtn.onclick = function() {
            console.log("Closing chat sidebar");
            chatSidebar.classList.remove("active");

            // stomp 연결 종료
            if (chatbotStompClient) {
                chatbotStompClient.disconnect();
                chatbotStompClient = null;
            }

            // 채팅 메시지 초기화
            const chatMessages = document.querySelector(".chat-messages");
            if (chatMessages) {
                chatMessages.innerHTML = ''; // 모든 메시지 제거
            }
        }
    }

    // 전송 버튼 클릭 이벤트
    if (sendBtn) {
        sendBtn.onclick = sendMessage;
    }

    // Enter 키 이벤트
    if (chatInput) {
        chatInput.addEventListener("keydown", function(event) {
            if (event.key === "Enter") {
                event.preventDefault();
                sendMessage();
            }
        });
    }
});