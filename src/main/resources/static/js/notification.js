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
            handleNotification(data.message, data.url, data.notificationId, true, data.type);  // 알림 처리

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


    // 기본 아이콘을 정의합니다.
    let icon = '';

    // 타입에 따라 아이콘을 설정합니다.
    if (type === 'ANSWER' || type === 'REPLY' || type === 'QUESTION') {
        icon = `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="26" fill="currentColor" class="bi bi-question" viewBox="0 0 18 18">
                    <path d="M5.255 5.786a.237.237 0 0 0 .241.247h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286m1.557 5.763c0 .533.425.927 1.01.927.609 0 1.028-.394 1.028-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94"/>
                </svg>`;
    } else if (type === 'SALE') {
        icon = `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="26" fill="currentColor" class="bi bi-coin" viewBox="0 0 18 18">
                    <path d="M5.5 9.511c.076.954.83 1.697 2.182 1.785V12h.6v-.709c1.4-.098 2.218-.846 2.218-1.932 0-.987-.626-1.496-1.745-1.76l-.473-.112V5.57c.6.068.982.396 1.074.85h1.052c-.076-.919-.864-1.638-2.126-1.716V4h-.6v.719c-1.195.117-2.01.836-2.01 1.853 0 .9.606 1.472 1.613 1.707l.397.098v2.034c-.615-.093-1.022-.43-1.114-.9zm2.177-2.166c-.59-.137-.91-.416-.91-.836 0-.47.345-.822.915-.925v1.76h-.005zm.692 1.193c.717.166 1.048.435 1.048.91 0 .542-.412.914-1.135.982V8.518z"/>
                    <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
                    <path d="M8 13.5a5.5 5.5 0 1 1 0-11 5.5 5.5 0 0 1 0 11m0 .5A6 6 0 1 0 8 2a6 6 0 0 0 0 12"/>
                </svg>`;
    } else if (type === 'REPORT') {
        icon = `<svg xmlns="http://www.w3.org/2000/svg" width="22" height="26" fill="currentColor" class="bi bi-shield-exclamation" viewBox="0 0 18 18">
                    <path d="M5.338 1.59a61 61 0 0 0-2.837.856.48.48 0 0 0-.328.39c-.554 4.157.726 7.19 2.253 9.188a10.7 10.7 0 0 0 2.287 2.233c.346.244.652.42.893.533q.18.085.293.118a1 1 0 0 0 .101.025 1 1 0 0 0 .1-.025q.114-.034.294-.118c.24-.113.547-.29.893-.533a10.7 10.7 0 0 0 2.287-2.233c1.527-1.997 2.807-5.031 2.253-9.188a.48.48 0 0 0-.328-.39c-.651-.213-1.75-.56-2.837-.855C9.552 1.29 8.531 1.067 8 1.067c-.53 0-1.552.223-2.662.524zM5.072.56C6.157.265 7.31 0 8 0s1.843.265 2.928.56c1.11.3 2.229.655 2.887.87a1.54 1.54 0 0 1 1.044 1.262c.596 4.477-.787 7.795-2.465 9.99a11.8 11.8 0 0 1-2.517 2.453 7 7 0 0 1-1.048.625c-.28.132-.581.24-.829.24s-.548-.108-.829-.24a7 7 0 0 1-1.048-.625 11.8 11.8 0 0 1-2.517-2.453C1.928 10.487.545 7.169 1.141 2.692A1.54 1.54 0 0 1 2.185 1.43 63 63 0 0 1 5.072.56"/>
                    <path d="M7.001 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.553.553 0 0 1-1.1 0z"/>
                </svg>`;
    }


    // HTML 메시지를 그대로 삽입
    const htmlMessage = `<a href="${url}" target="_blank">
            ${icon}
            ${message}
            </a>`; // 게시글 링크와 메시지 결합
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

       // 웹소켓 알림일 경우, 맨 위에 추가
       if (isWebsocket) {
        notificationList.insertBefore(newNotification, notificationList.firstChild); // 맨 위에 추가
    } else {
        notificationList.appendChild(newNotification); // 기존의 알림들은 아래에 추가
    }

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