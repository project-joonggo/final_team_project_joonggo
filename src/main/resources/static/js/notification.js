var socket = new SockJS('/notifications');
var stompClient = Stomp.over(socket);
var userId = null;

let notifications = []; // 알림을 저장하는 배열
let unreadCount = 0; // 읽지 않은 알림 수


// 페이지 로드 시 초기 알림을 서버에서 가져오기
window.onload = function() {
    fetchNotifications();
};

document.addEventListener('DOMContentLoaded', function() {
    // 알림 버튼 클릭 시 알림 리스트 토글
    document.getElementById("notification-icon").addEventListener("click", function() {
        toggleNotifications(); // 알림 리스트 보이기/숨기기
    });
});


stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    // frame에서 user-name을 가져와서 userId에 할당
    userId = frame.headers['user-name'];
    console.log('User ID:', userId);

    // 구독할 경로를 확인
    const subscriptionPath ="/user/"+userId+"/queue/notifications";
    console.log('Subscribing to:', subscriptionPath);  // 확인: 구독 경로가 올바르게 설정되었는지


    // /user/{userId}/queue/notifications 경로를 구독
    stompClient.subscribe(subscriptionPath, function (message) {
        console.log('Received message:', message);
        if (message.body) {
            console.log('Message body:', message.body);
            handleNotification(message.body);
        } else {
            console.log('Message body is empty or undefined');
        }

    });


}, function(error) {
    console.log("Error connecting to WebSocket: " + error);

});

stompClient.onclose = function() {
    console.log('WebSocket connection closed');
};



function handleNotification(message) {
    console.log('Handling notification:', message);

    // 메시지 그대로 삽입 (HTML로 처리)
    const notificationList = document.getElementById("notification-list");

    // 알림 항목 추가
    const newNotification = document.createElement("li");
    newNotification.classList.add("notification-item");
    newNotification.setAttribute("data-status", "unread"); // 상태를 '읽지 않음'으로 설정

    // HTML 메시지를 그대로 삽입
    newNotification.innerHTML = message; // HTML 메시지를 삽입


     // 알림 클릭 시 읽음 처리 및 URL로 이동
     newNotification.addEventListener("click", function() {
        markAsRead(newNotification);
        // 링크를 클릭하면 해당 URL로 이동
        const link = newNotification.querySelector("a");
        if (link) {
            window.location.href = link.href;
        }
    });

    notificationList.appendChild(newNotification);

    // 알림 배지 업데이트 (읽지 않은 알림 개수)
    unreadCount++;
    updateNotificationBadge();
}


// 알림 배지 업데이트
function updateNotificationBadge() {
    const badge = document.getElementById("notification-badge");
    if (unreadCount > 0) {
        badge.style.display = 'inline';  // 배지 표시
        badge.textContent = unreadCount; // 배지에 읽지 않은 알림 수 표시
    } else {
        badge.style.display = 'none';  // 읽을 알림이 없으면 배지 숨김
    }
}


// 알림 리스트를 토글하는 함수
function toggleNotifications() {
    const notificationList = document.getElementById("notification-list");
    if (notificationList.style.display === 'none') {
        notificationList.style.display = 'block';
    } else {
        notificationList.style.display = 'none';
    }
}

function markAsRead(notificationElement) {
    notificationElement.setAttribute("data-status", "read"); // 상태를 '읽음'으로 변경
    notificationElement.style.fontStyle = "italic";  // 읽은 알림을 구분하기 위한 스타일 (선택 사항)

    // 읽은 알림 수 감소
    unreadCount--;
    updateNotificationBadge(); // 배지 업데이트
}

// "새로운 알림이 없습니다." 메시지 표시
function showNoNotificationsMessage() {
    const notificationList = document.getElementById("notification-list");
    
    // "새로운 알림이 없습니다." 메시지 항목 추가
    const noNotificationMessage = document.createElement("li");
    noNotificationMessage.classList.add("notification-item");
    noNotificationMessage.textContent = "새로운 알림이 없습니다.";
    
    notificationList.appendChild(noNotificationMessage);
}



// 초기 알림을 서버에서 가져오기
function fetchNotifications() {
    // 서버에서 알림을 받아오는 AJAX 요청
    fetch("/notice") // 기존의 REST API 주소를 사용
        .then(response => response.json())
        .then(data => {
            console.log("Initial notifications:", data);
            notifications = data;  // 받은 알림 데이터를 저장
            if (notifications.length === 0) {
                showNoNotificationsMessage(); // 알림이 없으면 "새로운 알림이 없습니다." 메시지 표시
            } else {
                notifications.forEach(notification => handleNotification(notification.message)); // 각 알림을 화면에 표시
            }
        })
        .catch(error => {
            console.error("Error fetching notifications:", error);
        });
}
