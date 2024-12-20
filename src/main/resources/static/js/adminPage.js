
console.log("admin js in~!!");

document.addEventListener('DOMContentLoaded', function() {
    // 사이드 메뉴 링크 클릭 시
    const sideMenuLinks = document.querySelectorAll('.side-menu-link');

    console.log(sideMenuLinks);

    sideMenuLinks.forEach(function(link) {
        link.addEventListener('click', function(event) {
            event.preventDefault();  // 기본 동작(페이지 이동)을 막음
            const url = this.getAttribute('data-url');  // 클릭된 링크의 URL을 가져옴

            console.log(url);

            // AJAX 요청 (XMLHttpRequest 사용)
            const xhr = new XMLHttpRequest();
            xhr.open('GET', url, true);
            xhr.onload = function() {
                if (xhr.status === 200) {
                    // 응답받은 HTML을 파싱하여 content 부분만 추출
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(xhr.responseText, 'text/html');
                    const content = doc.querySelector('#content');  // 이제 #content로 선택
                    const pagination = doc.querySelector('.pagination');  // 페이지네이션도 추출

                    console.log(content);
                    console.log(pagination);

                    // content가 존재하면 #contentArea에 삽입
                    if (content) {
                        document.getElementById('contentArea').innerHTML = content.innerHTML;
                    } else {
                        console.error("Content not found in the response.");
                    }

                     // 페이지네이션 갱신
                     if (pagination) {
                        // .pagination을 현재 페이지네이션 영역에 덮어쓰기
                        document.querySelector('.pagination').innerHTML = pagination.innerHTML;
                    }

                     // 페이지네이션 링크 클릭 처리
                     setupPaginationLinks(); // 페이지네이션 링크를 다시 설정

                } else {
                    alert('페이지를 불러오는 데 실패했습니다.');
                }
            };
            xhr.onerror = function() {
                alert('네트워크 오류가 발생했습니다.');
            };
            xhr.send();
        });
    });


     // 페이지네이션 링크 클릭 시 (이벤트 위임 방식)
     function setupPaginationLinks() {
        const paginationContainer = document.querySelector('.pagination');

        console.log(paginationContainer);

        // 페이지네이션 링크 클릭 시 처리
        paginationContainer.addEventListener('click', function(event) {
            if (event.target && event.target.tagName === 'A') {
                event.preventDefault();
                const url = event.target.getAttribute('data-url');  // data-url에서 페이지 링크 가져오기
                console.log(url);

                // AJAX 요청
                const xhr = new XMLHttpRequest();
                xhr.open('GET', url, true);
                xhr.onload = function() {
                    if (xhr.status === 200) {
                        // 응답받은 HTML을 파싱하여 content 부분만 추출
                        const parser = new DOMParser();
                        const doc = parser.parseFromString(xhr.responseText, 'text/html');
                        const content = doc.querySelector('#content');  // content 부분 선택
                        const pagination = doc.querySelector('.pagination');  // 페이지네이션 부분 선택

                        if (content) {
                            // 새로운 content를 #contentArea에 삽입
                            document.getElementById('contentArea').innerHTML = content.innerHTML;
                        }

                        if (pagination) {
                            // 페이지네이션을 갱신
                            document.querySelector('.pagination').innerHTML = pagination.innerHTML;
                        }

                        // 페이지네이션 링크 클릭 처리
                        setupPaginationLinks(); // 페이지네이션 링크를 다시 설정
                    }
                };
                xhr.send();
            }
        });
    }

    // 페이지네이션 링크 클릭 시 처리
    setupPaginationLinks(); // 페이지네이션 이벤트 리스너 설정





    
});


