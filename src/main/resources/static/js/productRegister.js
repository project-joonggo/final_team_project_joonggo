console.log("product Reg js in ");

let oEditors = []

smartEditor = function() {
    console.log("Naver SmartEditor")
    nhn.husky.EZCreator.createInIFrame({
        oAppRef: oEditors,
        elPlaceHolder: "c",
        sSkinURI: "/smarteditor/SmartEditor2Skin.html",
        fCreator: "createSEditor2"

    })
}

document.addEventListener('DOMContentLoaded', function() {
    smartEditor();
});


document.getElementById("regBtn").addEventListener("click", () =>{
     // SmartEditor에서 콘텐츠를 업데이트
     oEditors.getById["c"].exec("UPDATE_CONTENTS_FIELD", []);

     // SmartEditor에서 내용을 가져옴
     let content = oEditors.getById("c").getIR(); // 에디터의 HTML 내용 가져오기
     let title = document.getElementById("t").value; // 제목 가져오기
     let writer = document.getElementById("w").value; // 작성자가져오기

     console.log("제목:", title);      // title 값 확인
     console.log("내용:", content);    // content 값 확인
     console.log("작성자:", writer);   // writer 값 확인

     if (content == '') {
        alert("내용을 입력해주세요.");
        oEditors.getById["c"].exec("FOCUS"); // 에디터로 포커스 이동
        return;
    } else {
        // POST 요청을 보낼 데이터 객체
        let formData = new FormData();
        formData.append("title", title);
        formData.append("content", content);
        formData.append("writer", writer);

        registerPostToServer(formData).then(result => {
            if(result){
                alert("글을 등록하였습니다.");
            } else {
                alert("게시글 등록 실패");
            }
        })
    }
});


async function registerPostToServer(formData) {
    try {
        const url = "/board/register";  // 요청 URL
        const config = {
            method: "POST",  // HTTP 메서드
            headers: {
                'Content-Type': 'multipart/form-data; charset=utf-8'  // JSON 형식으로 전송
            },
            body: formData
        };

        // 서버에 요청 보내기
        const resp = await fetch(url, config);

        // 응답 처리
        if (resp.ok) {  // 2xx 범위 응답이 올 때
            console.log('Success');
            alert('저장하였습니다.');
            window.location.href = "/"; // 등록 후 리다이렉트
        } else {
            console.log(resp);
            alert('오류가 발생하였습니다.');
        }

        // 응답 내용 읽기 (예: JSON 데이터)
        const result = await resp.json(); // 응답이 JSON이라면 `resp.json()` 사용
        console.log(result);
        return result;

    } catch (error) {
        console.log(error);  // 에러 발생 시 처리
        alert("서버와의 연결에 문제가 발생했습니다.");
    }
}

