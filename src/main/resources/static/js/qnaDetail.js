console.log("qnaDetail .js in !");


const asAddBtn = document.getElementById('asAddBtn');
if (asAddBtn) {
    asAddBtn.addEventListener('click', () => {
        const answerText = document.getElementById('answerText');

        let asData = {
            qnaId: qnaVal,
            userNum: userVal,
            answer: answerText.value
        };

        postAnswerToServer(asData).then(result => {
            if (result !== "0") {
                alert("답변 등록 성공");
                answerText.value = "";
            } else {
                alert("답변 등록 실패");
            }
            spreadAnswer(qnaVal);
        });
    });
}



// 답변 뿌리기
function spreadAnswer(qnaId){
    getAnswerFromServer(qnaId).then(result => {
        console.log(result);
        const ul = document.getElementById('asListArea');
        if( result.length > 0 ){
            ul.innerHTML = ""; 
            for(let avo of result){
                console.log(avo);
                let li = `<li class="list-group-item">`;
                li += `<div class="d-flex align-items-center justify-content-between replyToggle" style="width: 100%;" data-ano=${avo.ano} onclick="toggleReplyForm(${avo.ano})">`;
                li += `<div class="d-flex gap-3">`;
                li += `<div class="fw-bold">관리자</div>`;
                li += `${avo.answer}`;
                // 날짜 포맷 변환
                const regAtDate = new Date(avo.regAt);
                const formattedDate = regAtDate.toLocaleString('ko-KR', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit',
                    hour12: false // 24시간 형식
                });
                li += `</div>`;
                li += `<div class="d-flex gap-3">`;
                li += `<span class="d-flex answerDate align-items-center">${formattedDate}</span>`;
                // 수정 삭제 버튼 추가
                if (isAdmin){
                    li += `<div class="d-grid gap-1 d-md-flex justify-content-md-end answerButtons">`;
                    li += `<button type="button" data-ano=${avo.ano} class="btn joinPostBtn mod" data-bs-toggle="modal" data-bs-target="#myModal">수정</button>`;
                    li += `<button type="button" data-ano=${avo.ano} class="btn joinPostBtn del" onclick="return confirm('정말 삭제하시겠습니까?')">삭제</button>`;
                    li += `</div>`;
                }
                li += `</div>`;
                li += `</div>`;

                // 추가질문 입력 폼 (기본적으로 숨김)
                li += `<div id="replyForm-${avo.ano}" class="ms-3 mt-2 justify-content-between align-items-center gap-2" style="display:none;">
                           <div>
                               <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#A0173B" class="bi bi-arrow-return-right" viewBox="0 0 16 16">
                                 <path fill-rule="evenodd" d="M1.5 1.5A.5.5 0 0 0 1 2v4.8a2.5 2.5 0 0 0 2.5 2.5h9.793l-3.347 3.346a.5.5 0 0 0 .708.708l4.2-4.2a.5.5 0 0 0 0-.708l-4-4a.5.5 0 0 0-.708.708L13.293 8.3H3.5A1.5 1.5 0 0 1 2 6.8V2a.5.5 0 0 0-.5-.5"/>
                               </svg>
                           </div>
                           <div class="d-flex align-items-center justify-content-between replyArea gap-2">
                               <textarea class="replyText" id="replyText-${avo.ano}" placeholder="추가질문을 작성하세요"></textarea>
                               <button class="btn addReplyBtn" onclick="addReply(${avo.ano})">등록</button>
                           </div>
                       </div>`;

                // 추가질문 목록 표시 영역
                li += `<ul id="replies-${avo.ano}" class="list-group mt-3"></ul>`;

                li += `</li>`;
                ul.innerHTML += li;

                 // 추가질문 목록 불러오기
                 loadReplies(avo.ano);
            }
        } else{
            ul.innerHTML = `<li class="list-group-item">아직 답변이 없습니다.</li>`;
        }
    })
}


document.addEventListener('click', (e) => {


    if(e.target.classList.contains('del')){
        let ano = e.target.closest('li').dataset.ano;
        console.log(ano);
        deleteAnswerToServer(ano).then(result => {
            if(result >0){
                alert("답변 삭제 성공");
                spreadAnswer(qnaVal);
            } else{
                alert("답변 삭제 실패");
            }
        })

    }
    if(e.target.classList.contains('mod')){

        let li = e.target.closest('li');
        
        let answerWriter = li.querySelector('.fw-bold').innerText;
        document.getElementById('asWriterMod').innerHTML = "관리자";

        let answerText = li.querySelector('.fw-bold').nextSibling;
        document.getElementById('asTextMod').value = answerText.nodeValue;

        document.getElementById('asModBtn').setAttribute("data-ano", li.dataset.ano);

    }

    if(e.target.id == 'asModBtn'){
        let asData = {
            ano: e.target.dataset.ano,  
            answer: document.getElementById('asTextMod').value
        }
        console.log(asData);
        modifyAnswerToServer(asData).then(result => {
            if(result == '1'){
                alert("답변 수정 성공");
            } else{
                alert("답변 수정 실패");
            }    
            // 모달창 닫기
            document.querySelector('.btn-close').click();
            // 댓글 뿌리기
            spreadAnswer(qnaVal);
        })
    }

})



// 댓글 등록 
async function postAnswerToServer(asData) {
    try {
        const url = "/answer/post"
        const config = {
            method: 'post',
			headers: {
				'Content-Type': 'application/json; charset=utf-8'
			},
			body: JSON.stringify(asData)
        };
        const resp = await fetch(url, config);
        console.log(resp);
        const result = await resp.text(); 
        return result;
    } catch (error) {
        console.log(error);
    }
}


// 댓글 리스트 가져오기
async function getAnswerFromServer(qnaId) {
    try {
        const resp = await fetch("/answer/list/"+ qnaId) 
        const result = await resp.json();
        return result;
    } catch (error) {
        console.log(error);
    }
}



async function deleteAnswerToServer(ano) {
    try{
        const url = "/answer/delete/"+ ano;
        const config = {
            method:'delete'
        }
        const resp = await fetch(url,config);
        const result = await resp.text();
        return result;

    } catch(error){
        console.log(error);
    }
}


async function modifyAnswerToServer(asData) {
    try {
        const url = "/answer/modify";
        const config ={
            method : "put",
            headers : {
                'Content-Type' : 'application/json; charset=utf-8'
            },
            body : JSON.stringify(asData)
        };
        const resp = await fetch(url,config);
        const result = await resp.text();

       return result; 

    } catch (error) {
        console.log(error);
    }
}


// 추가질문 입력 폼 토글
function toggleReplyForm(ano) {
    var replyForm = document.getElementById("replyForm-" + ano);
    replyForm.style.display = (replyForm.style.display === "none" || replyForm.style.display === "") ? "flex" : "none";
}


// 추가질문 추가
function addReply(ano) {
    var replyText = document.getElementById("replyText-" + ano).value;

    if (!replyText) {
        alert("추가질문 내용을 입력해주세요.");
        return;
    }
    console.log(replyText);

    // 추가질문 데이터 객체
    const replyData = {
        ano: ano,  // 원본 답변 번호
        userNum: userVal, // 사용자 번호
        reply: replyText // 추가질문 내용
    };

    // 비동기 요청으로 추가질문 서버에 추가
    postReplyToServer(replyData)
        .then(result => {
            if (result === "1") {
                alert("추가질문이 등록되었습니다.");
                loadReplies(ano);  // 추가질문 목록 새로고침
                document.getElementById("replyText-" + ano).value = "";
            } else {
                alert("추가질문 등록 실패");
            }
        });
}

// 추가질문 추가 비동기
async function postReplyToServer(replyData) {
    try {
        const url = "/reply/add";
        const config = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify(replyData)
        };

        const resp = await fetch(url, config);
        console.log(resp);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}


// 추가질문 리스트 가져오기
async function getRepliesFromServer(ano) {
    try {
        const resp = await fetch(`/reply/list/${ano}`); // 추가질문 가져오는 API 경로
        const result = await resp.json();
        return result;
    } catch (error) {
        console.log("추가질문을 가져오는 중 오류가 발생했습니다:", error);
    }
}


// 추가질문 목록 로드
async function loadReplies(ano) {
    const replies = await getRepliesFromServer(ano); // 서버에서 추가질문 가져오기
    const repliesList = document.getElementById(`replies-${ano}`);

    if (replies && replies.length > 0) {
        repliesList.innerHTML = ''; // 기존 추가질문 목록 초기화
        renderReplies(repliesList, replies); // 가져온 추가질문 화면에 렌더링
    } else {
        repliesList.innerHTML = '';
    }
}

// 추가질문 렌더링
function renderReplies(repliesList, replies) {
    replies.forEach(reply => {
        let li = document.createElement('li');
        li.classList.add('list-group-item');
        li.innerHTML = `
            <div class="ms-2 me-auto">
                <div class="fw-bold">${reply.writerName}</div>
                ${reply.reply}
            </div>
            <span class="badge text-bg-primary rounded-pill">${reply.regAt}</span>
        `;

      // 수정/삭제 버튼에 id 추가
      if (isAdmin || reply.userNum === userVal){
          li.innerHTML += `
          <div class="d-grid gap-2 d-md-flex justify-content-md-end">
              <button type="button" id="modify-reply-${reply.replyId}" class="btn btn-outline-warning btn-sm" data-replyId="${reply.replyId}" onclick="modifyReply(${reply.replyId})">수정</button>
              <button type="button" id="delete-reply-${reply.replyId}" class="btn btn-outline-danger btn-sm" data-replyId="${reply.replyId}" onclick="deleteReply(${reply.replyId})">삭제</button>
          </div>
           `;
      }
        repliesList.appendChild(li);
    });
}

// 대댓글 수정 함수
async function modifyReply(replyId) {
    const replyTextElement = document.getElementById(`reply-text-${replyId}`);
    const replyText = replyTextElement ? replyTextElement.innerText : '';

    // 수정 폼에 텍스트를 채운다.
    const modifiedText = prompt("수정할 대댓글을 입력해주세요", replyText);

    if (modifiedText !== null) {
        const replyData = {
            replyId: replyId,  // replyId 사용
            reply: modifiedText
        };

        console.log(replyData);
        // 서버로 대댓글 수정 요청
        const result = await modifyReplyToServer(replyData);
        
        if (result === '1') {
            alert("대댓글 수정 성공");
            spreadAnswer(qnaVal); // 대댓글 목록을 다시 불러옴
        } else {
            alert("대댓글 수정 실패");
        }
    }
}

// 서버로 대댓글 수정 요청 보내는 함수
async function modifyReplyToServer(replyData) {
    try {
        const url = "/reply/modify";
        const config = {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify(replyData)
        };

        const resp = await fetch(url, config);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}

// 대댓글 삭제 함수
async function deleteReply(replyId) {
    const confirmDelete = confirm("대댓글을 삭제하시겠습니까?");
    
    if (confirmDelete) {
        const result = await deleteReplyToServer(replyId);
        
        if (result === '1') {
            alert("대댓글 삭제 성공");
            spreadAnswer(qnaVal); // 대댓글 목록을 다시 불러옴
        } else {
            alert("대댓글 삭제 실패");
        }
    }
}

// 서버로 대댓글 삭제 요청 보내는 함수
async function deleteReplyToServer(replyId) {
    try {
        const url = `/reply/delete/${replyId}`;
        const config = {
            method: 'DELETE'
        };

        const resp = await fetch(url, config);
        const result = await resp.text();
        return result;
    } catch (error) {
        console.log(error);
    }
}