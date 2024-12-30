var socket = new SockJS('/notifications');
var stompClient = Stomp.over(socket);
var userId = null;

let notifications = []; // 알림을 저장하는 배열
let dbNotificationsCount = 0; // DB에서 받은 알림 수
let websocketNotificationsCount = 0; // 웹소켓으로 받은 알림 수
let unreadCount = 0; // 읽지 않은 알림 수


document.addEventListener('DOMContentLoaded', function () {
    fetchNotifications(); // 페이지가 로드될 때마다 알림 가져오기
    
    // 알림 버튼 클릭 시 알림 리스트 토글
    document.getElementById("notification-icon").addEventListener("click", function() {
        console.log(userId);
        if (!userId) {  // 로그인 상태 확인
            // 로그인하지 않았다면 로그인 페이지로 리디렉션
            window.location.href = "/user/login";
        } else {
            toggleNotifications(); // 알림 리스트 보이기/숨기기
        }
    });

        // 각 카테고리별 필터링 버튼 클릭 이벤트 추가
        document.getElementById("show-all-notifications").addEventListener("click", function() {
            showAllNotifications(); // 모든 알림을 표시
        });
    
        document.getElementById("show-sale-notifications").addEventListener("click", function() {
            showNotificationsByType('SALE'); // 판매 알림만 표시
        });
    
        document.getElementById("show-question-notifications").addEventListener("click", function() {
            showNotificationsByType('QUESTION'); // 문의 알림만 표시
        });
    
        document.getElementById("show-report-notifications").addEventListener("click", function() {
            showNotificationsByType('REPORT'); // 신고 알림만 표시
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
            const data = JSON.parse(message.body); // 서버에서 JSON으로 보낸다고 가정
            handleNotification(data.message, data.url, data.notificationId, true);  // 알림 처리

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



function handleNotification(message,url,notificationId,isWebsocket, type) {
    console.log('Handling notification:', message);
    console.log("url: ",url);
    console.log("notificationId: ",notificationId);

    let notificationList = null;

    // 'ANSWER'와 'REPLY' 알림은 모두 'QUESTION' 카테고리 내에서 처리
    if (type === 'ANSWER' || type === 'REPLY') {
        notificationList = document.getElementById("QUESTION-notifications"); // 'QUESTION' 카테고리 안에 포함
    } else {
        notificationList = document.getElementById(type + "-notifications"); // 일반 카테고리 처리
    }
    
    const newNotification = document.createElement("li");
    newNotification.classList.add("notification-item");
    newNotification.setAttribute("data-status", "unread"); // 상태를 '읽지 않음'으로 설정
    newNotification.setAttribute("data-id", notificationId); // notificationId를 data-id 속성에 저장

    // HTML 메시지를 그대로 삽입
    const htmlMessage = `<a href="${url}" target="_blank">${message}</a>`; // 게시글 링크와 메시지 결합
    newNotification.innerHTML = htmlMessage; // HTML 메시지 삽입

     // 알림 클릭 시 읽음 처리 및 URL로 이동
     newNotification.addEventListener("click", function() {
        const notificationId = newNotification.getAttribute("data-id"); // 클릭한 알림의 ID 가져오기
        markAsRead(newNotification,notificationId);
        // 링크를 클릭하면 해당 URL로 이동
        const link = newNotification.querySelector("a");
        if (link) {
            window.location.href = link.href;
        }
    });

    notificationList.appendChild(newNotification);

    // 알림 배지 업데이트 (읽지 않은 알림 개수)

     // 웹소켓 알림일 경우에만 카운트 증가
     if (isWebsocket) {
        websocketNotificationsCount++;  // 웹소켓 알림 카운트 증가
    }
    unreadCount++;  // 읽지 않은 알림 수 증가
    updateNotificationBadge();
    hideNoNotificationsMessage();
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

function markAsRead(notificationElement, notificationId) {
    notificationElement.setAttribute("data-status", "read"); // 상태를 '읽음'으로 변경
    notificationElement.style.fontStyle = "italic";  // 읽은 알림을 구분하기 위한 스타일 (선택 사항)

    // 서버에 읽음 상태로 변경 요청
    fetch(`/notice/read/${notificationId}`, {
        method: 'POST'
    })
    .then(response => {
        if (response.ok) {
            console.log("Notification marked as read successfully");
        } else {
            console.error("Failed to mark notification as read");
        }
    })
    .catch(error => {
        console.error("Error marking notification as read:", error);
    });

    // 읽은 알림 수 감소
    unreadCount--;
    updateNotificationBadge(); // 배지 업데이트
}

// "새로운 알림이 없습니다." 메시지 표시
function showNoNotificationsMessage() {

    if (dbNotificationsCount === 0 && websocketNotificationsCount === 0) {
        
        const notificationList = document.getElementById("notification-list");
        
        // "새로운 알림이 없습니다." 메시지 항목 추가
        const noNotificationMessage = document.createElement("li");
        noNotificationMessage.classList.add("notification-item");
        noNotificationMessage.textContent = "새로운 알림이 없습니다.";
        
        notificationList.appendChild(noNotificationMessage);
    }
}

// "새로운 알림이 없습니다." 메시지 숨기기
function hideNoNotificationsMessage() {
    // DB에서 알림이 없고, 웹소켓 알림을 받은 경우 메시지를 숨김
    if (dbNotificationsCount === 0 && websocketNotificationsCount > 0) {
        const notificationList = document.getElementById("notification-list");
        const noNotificationMessage = notificationList.querySelector(".notification-item");
        if (noNotificationMessage) {
            noNotificationMessage.remove(); // 메시지를 숨김
        }
    }
}


// 초기 알림을 서버에서 가져오기
function fetchNotifications() {
    // 서버에서 알림을 받아오는 AJAX 요청
    fetch("/notice") // 기존의 REST API 주소를 사용
        .then(response => response.json())
        .then(data => {
            console.log("Initial notifications:", data);
            notifications = data;  // 받은 알림 데이터를 저장
            dbNotificationsCount = notifications.length; 
            if (dbNotificationsCount === 0) {
                showNoNotificationsMessage(); // 알림이 없으면 "새로운 알림이 없습니다." 메시지 표시
            } else {
                notifications.forEach(notification => handleNotification(notification.message, notification.url, notification.notificationId, false, notification.type)); // 각 알림을 화면에 표시
            }
        })
        .catch(error => {
            console.error("Error fetching notifications:", error);
        });
}


// 모든 알림을 표시
function showAllNotifications() {
    document.getElementById("SALE-notifications").style.display = 'block';
    document.getElementById("QUESTION-notifications").style.display = 'block';
    document.getElementById("REPORT-notifications").style.display = 'block';
}

// 특정 카테고리의 알림만 표시
function showNotificationsByType(type) {
    const allCategories = ['SALE', 'QUESTION', 'REPORT'];
    
    // 모든 카테고리를 숨김
    allCategories.forEach(category => {
        document.getElementById(category + "-notifications").style.display = 'none';
    });

    // 선택한 카테고리만 표시
    document.getElementById(type + "-notifications").style.display = 'block';
}

document.addEventListener('DOMContentLoaded', function() {
    // 카테고리 버튼들 가져오기
    const categoryButtons = document.querySelectorAll('.filter-button');

    // 버튼 클릭 시 active 클래스를 추가하고, 다른 버튼에서 제거
    categoryButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 모든 버튼에서 active 클래스 제거
            categoryButtons.forEach(btn => btn.classList.remove('active'));

            // 클릭된 버튼에 active 클래스 추가
            button.classList.add('active');
        });
    });
});