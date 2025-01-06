// WebSocket 연결 및 구독 관리를 위한 클래스
class ChatWebSocketManager {
    constructor() {
        this.socket = null;
        this.stompClient = null;
        this.subscriptions = new Map();
        this.connectedCallback = null;
        this.MAX_RECONNECT_ATTEMPTS = 5;
        this.reconnectCount = 0;
    }

    // WebSocket 연결 초기화 - 채팅 전용 엔드포인트 사용
    async connect(userNum) {
        try {
            // 채팅 전용 웹소켓 엔드포인트 사용
            this.socket = new SockJS('/ws/chat');  // 채팅 전용 엔드포인트
            this.stompClient = Stomp.over(this.socket);
            this.stompClient.debug = null;

            return new Promise((resolve, reject) => {
                this.stompClient.connect(
                    {},
                    frame => {
                        console.log('Chat WebSocket Connected: ' + frame);
                        this.reconnectCount = 0;

                        // 채팅 관련 구독만 처리
                        this.subscribeToUnreadCount(userNum);

                        // 채팅방 목록이 있는 경우 각 채팅방별 구독 추가
                        this.subscribeToRoomUnreadCounts(userNum);

                        if (this.connectedCallback) {
                            this.connectedCallback();
                        }
                        resolve();
                    },
                    error => {
                        console.error('Chat STOMP connection error:', error);
                        if (this.reconnectCount < this.MAX_RECONNECT_ATTEMPTS) {
                            setTimeout(() => {
                                this.reconnectCount++;
                                this.connect(userNum);
                            }, 3000);
                        } else {
                            reject(error);
                        }
                    }
                );
            });
        } catch (error) {
            console.error('Chat WebSocket connection error:', error);
            return Promise.reject(error);
        }
    }

    // 채팅방별 읽지 않은 메시지 수 구독
    subscribeToRoomUnreadCounts(userNum) {
        const roomList = document.getElementById('roomList');
        if (roomList) {
            const rooms = roomList.getElementsByClassName('room-list-item');
            Array.from(rooms).forEach(room => {
                const roomId = room.getAttribute('data-room-id');

                if (roomId) {
                    this.stompClient.subscribe(`/topic/chat/${roomId}`, message => {
                        const data = JSON.parse(message.body);
                        const roomUnreadCount = data.roomUnreadCount;
                        const totalUnreadCount = data.totalUnreadCount;

                        // HTML에 badge span이 있는 경우에만 업데이트 (수신자인 경우만 badge span이 존재)
                        const badge = room.querySelector('.badge');
                        if (badge) {
                            badge.textContent = roomUnreadCount > 0 ? roomUnreadCount : '';
                            badge.style.display = roomUnreadCount > 0 ? 'inline' : 'none';
                            this.updateHeaderBadge(totalUnreadCount);
                        }
                    });
                }
            });
        }
    }


    // 읽지 않은 채팅 메시지 수 구독
    subscribeToUnreadCount(userNum) {
        if (this.stompClient && userNum) {
            // 채팅 관련 구독 경로 사용
            this.stompClient.subscribe(`/topic/user/${userNum}/unread`, message => {
                const unreadCount = JSON.parse(message.body);
                this.updateHeaderBadge(unreadCount);
            });

            // 초기 읽지 않은 메시지 수 조회
            this.fetchUnreadCount(userNum);
        }
    }

    // 읽지 않은 채팅 메시지 수 업데이트
    updateHeaderBadge(count) {
        const headerBadge = document.getElementById('headerUnreadBadge');
        if (headerBadge) {
            headerBadge.textContent = count > 0 ? count : '';
            headerBadge.style.display = count > 0 ? 'inline' : 'none';
        }
    }

    // 초기 읽지 않은 채팅 메시지 수 조회
    async fetchUnreadCount(userNum) {
        try {
            const response = await fetch(`/chat/unread/total?userNum=${userNum}`);
            if (!response.ok) throw new Error('Network response was not ok');
            const count = await response.json();
            this.updateHeaderBadge(count);
            await this.updateRoomUnreadCounts(userNum);
        } catch (error) {
            console.error('Error fetching unread chat count:', error);
        }
    }

    // 채팅방별 읽지 않은 메시지 수 업데이트
    async updateRoomUnreadCounts(userNum) {
        try {
            const response = await fetch(`/chat/unread/rooms?userNum=${userNum}`);
            if (!response.ok) throw new Error('Network response was not ok');
            const unreadCounts = await response.json();

            // 각 채팅방의 배지 업데이트
            Object.entries(unreadCounts).forEach(([roomId, count]) => {
                const room = document.querySelector(`li[data-room-id="${roomId}"]`);
                if (room) {
                    const badge = room.querySelector('.badge');
                    if (badge) {
                        badge.textContent = count > 0 ? count : '';
                        badge.style.display = count > 0 ? 'inline' : 'none';
                    }
                }
            });
        } catch (error) {
            console.error('Error fetching room unread counts:', error);
        }
    }

    // 연결 해제
    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.subscriptions.clear();
            this.stompClient = null;
            this.socket = null;
        }
    }
}

// ChatWebSocketManager 인스턴스 생성
const chatWebSocketManager = new ChatWebSocketManager();

// DOM 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    const chatLink = document.querySelector('a[data-user-num]');
    if (chatLink) {
        const userNum = chatLink.dataset.userNum;
        chatWebSocketManager.connect(userNum);
    }
});

// 페이지 언로드 시 연결 해제
window.addEventListener('beforeunload', function() {
    chatWebSocketManager.disconnect();
});