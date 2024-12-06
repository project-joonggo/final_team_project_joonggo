let stompClient = null;

function setConnected(connected) {
    // #connect 버튼의 disabled 속성 설정
    document.getElementById("connect").disabled = connected;

    // #disconnect 버튼의 disabled 속성 설정
    document.getElementById("disconnect").disabled = !connected;

    // #send 버튼의 disabled 속성 설정
    document.getElementById("send").disabled = !connected;

    // #conversation의 표시 여부 설정
    if (connected) {
        document.getElementById("conversation").style.display = "block";
    } else {
        document.getElementById("conversation").style.display = "none";
    }

    // #msg 내용 초기화
    document.getElementById("msg").innerHTML = "";
}

function connect() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        // 서버로부터 메시지를 수신하면 showMessage 호출
        stompClient.subscribe('/topic/public', function (message) {
            showMessage("받은 메시지: " + message.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    // #msg의 값 가져오기
    let message = document.getElementById("msg").value;
    showMessage("보낸 메시지: " + message);

    // 서버에 메시지 전송
    stompClient.send("/app/sendMessage", {}, JSON.stringify(message));
}

function showMessage(message) {
    // #communicate 테이블에 새로운 메시지 추가
    let communicateTable = document.getElementById("communicate");
    let newRow = communicateTable.insertRow();
    let cell = newRow.insertCell(0);
    cell.textContent = message;
}

// DOMContentLoaded 이벤트를 사용하여 페이지 로드 후 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', function () {
    let form = document.querySelector("form");
    form.addEventListener('submit', function (e) {
        e.preventDefault();
    });

    document.getElementById("connect").addEventListener('click', connect);
    document.getElementById("disconnect").addEventListener('click', disconnect);
    document.getElementById("send").addEventListener('click', sendMessage);
});