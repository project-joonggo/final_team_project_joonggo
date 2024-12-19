console.log("myInfo.js in");
function showAlert(){
    alert("그동안 쓸템(SSeulTem)을 이용해 주셔서 감사합니다.");
    window.location.href = "/user/delete"; // 탈퇴 요청
}

var score = parseFloat(document.getElementById("score-value").textContent);
var percentage = (score / 100) * 100;

// 계산된 퍼센트를 게이지 바에 반영
document.getElementById("score-bar").style.width = percentage + "%";