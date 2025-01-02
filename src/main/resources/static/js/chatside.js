//console.log("chatside js conn");

// 대화방 요소와 버튼을 찾기

const openChatBtn = document.getElementById("openChatBtn");
const closeChatBtn = document.getElementById("closeChatBtn");
const chatSidebar = document.getElementById("chatSidebar");
const sendBtn = document.getElementById("sendBtn");
const chatInput = document.getElementById("chatInput");

// 대화방 열기 버튼 클릭 시
openChatBtn.onclick = function() {
  chatSidebar.classList.add("active");
}

// 대화방 닫기 버튼 클릭 시
closeChatBtn.onclick = function() {
  chatSidebar.classList.remove("active");

}

// 메시지 전송 버튼 클릭 시 (간단한 메시지 추가)
sendBtn.onclick = function() {
  const message = chatInput.value.trim();
  if (message !== "") {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message");
    messageElement.textContent = message;
    document.querySelector(".chat-messages").appendChild(messageElement);
    chatInput.value = ""; // 입력 필드 비우기
    chatInput.focus(); // 입력 필드에 포커스 유지
  }
}

// 입력 필드에서 Enter 키로 메시지 전송
chatInput.addEventListener("keydown", function(event) {
  if (event.key === "Enter") {
    sendBtn.click();
  }
});
