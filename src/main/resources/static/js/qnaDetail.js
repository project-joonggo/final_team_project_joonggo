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
                alert("댓글 등록 성공");
                answerText.value = "";
            } else {
                alert("댓글 등록 실패");
            }
            spreadAnswer(qnaVal);
        });
    });
}



// 댓글 뿌리기
function spreadAnswer(qnaId){
    getAnswerFromServer(qnaId).then(result => {
        console.log(result);
        const ul = document.getElementById('asListArea');
        if( result.length > 0 ){
            for(let avo of result){
                let li = `<li class="list-group-item" data-ano=${avo.ano}>`;
                li += `<div class="ms-2 me-auto">`;
                li += `<div class="fw-bold">관리자</div>`;
                li += `${avo.answer}`;
                li += `</div>`;
                li += `<span class="badge text-bg-primary rounded-pill">${avo.regAt}</span>`;
                // 수정 삭제 버튼 추가   
                if (isAdmin){
                    li += `<div class="d-grid gap-2 d-md-flex justify-content-md-end">`;
                    li += `<button type="button" data-ano=${avo.ano} class="btn btn-outline-warning btn-sm mod" data-bs-toggle="modal" data-bs-target="#myModal">%</button>`;
                    li += `<button type="button" data-ano=${avo.ano} class="btn btn-outline-danger btn-sm del">X</button>`;
                    li += `</div>`;
                }
                li += `</li>`;
                ul.innerHTML += li;           
            } 
        } else{
            ul.innerHTML = `<li class="list-group-item">아직 답변이 없습니다.</li>`;
        }
    })
}



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