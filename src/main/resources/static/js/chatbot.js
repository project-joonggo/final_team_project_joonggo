let chatBotStompClient = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('send').disabled = !connected;

    const conversation = document.getElementById('conversation');
    if (connected) {
        conversation.style.display = 'block';
    } else {
        conversation.style.display = 'none';
    }
    document.getElementById('msg').innerHTML = '';
}

function connect() {
    const socket = new SockJS('/ws/chatbot');
    chatBotStompClient = Stomp.over(socket);
    chatBotStompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        chatBotStompClient.subscribe('/topic/public', function (message) {
            showMessage("받은 메시지: " + message.body); //서버에 메시지 전달 후 리턴받는 메시지
        });
    });
}

function disconnect() {
    if (chatBotStompClient !== null) {
        chatBotStompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    const msgElement = document.getElementById('msg');
    const message = msgElement.value;
    showMessage("보낸 메시지: " + message);
    chatBotStompClient.send("/app/sendMessage", {}, JSON.stringify(message)); //서버에 보낼 메시지
}

function showMessage(message) {
    const communicateElement = document.getElementById('communicate');
    const row = document.createElement('tr');
    const cell = document.createElement('td');
    cell.textContent = message;
    row.appendChild(cell);
    communicateElement.appendChild(row);
}

// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 폼 제출 이벤트 방지
    document.querySelector('form').addEventListener('submit', function(e) {
        e.preventDefault();
    });

    // 버튼 이벤트 리스너 등록
    document.getElementById('connect').addEventListener('click', connect);
    document.getElementById('disconnect').addEventListener('click', disconnect);
    document.getElementById('send').addEventListener('click', sendMessage);
});