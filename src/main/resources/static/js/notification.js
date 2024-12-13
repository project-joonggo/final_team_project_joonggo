var socket = new SockJS('/notifications');
var stompClient = Stomp.over(socket);
var userId = null;

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    // frame에서 user-name을 가져와서 userId에 할당
    userId = frame.headers['user-name'];
    console.log('User ID:', userId);

    // 구독할 경로를 확인
    const subscriptionPath = '/user/' + userId + '/queue/notifications';
    console.log('Subscribing to:', subscriptionPath);  // 확인: 구독 경로가 올바르게 설정되었는지

    // /user/{userId}/queue/notifications 경로를 구독
    stompClient.subscribe(subscriptionPath, function (messageOutput) {
        console.log('Received message:', messageOutput);  // 메시지가 제대로 수신되었는지 확인
        console.log('Message body:', messageOutput.body);  // 메시지 본문 확인
        handleNotification(messageOutput.body);  // 알림 처리
    });

});



function handleNotification(message) {
    console.log('Handling notification:', message);
    const notificationBadge = document.getElementById("notification-badge");
    const notificationList = document.getElementById("notification-list");

    // 알림 아이콘 갯수 업데이트
    const currentCount = parseInt(notificationBadge.textContent) || 0;
    notificationBadge.textContent = currentCount + 1;
    notificationBadge.style.display = 'inline';  // 알림 배지 보이기

    // 알림 드롭다운에 새로운 알림 추가
    const listItem = document.createElement("li");
    listItem.classList.add("dropdown-item");
    listItem.textContent = message;
    notificationList.appendChild(listItem);

    // 알림 드롭다운 보이기
    document.getElementById("notification-dropdown").style.display = 'block';
}

// 서버로 알림 보내기 예시 (구매 시 발생)
function sendNotificationToServer(message) {
    console.log('Sending notification to server with message:', message);  // 확인: 서버로 메시지가 제대로 전송되었는지
    stompClient.send("/app/notifications", {}, JSON.stringify({'message': message}));
}


// 알림 리스트를 마우스 호버로 보이게 처리
function showNotifications() {
    const notificationList = document.getElementById("notification-list");
    notificationList.style.display = 'block';
}

function hideNotifications() {
    const notificationList = document.getElementById("notification-list");
    notificationList.style.display = 'none';
}