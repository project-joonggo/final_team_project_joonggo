console.log("admin js in~!!");

// 전역 변수로 isPending 상태 관리
let isPending = 'false';  // 기본값은 'false' (전체보기)

document.addEventListener('DOMContentLoaded', function() {

    loadContent('/user/list');  // 페이지 로드 시 유저 리스트를 자동으로 불러옴

    // 사이드 메뉴 링크 클릭 시
    const sideMenuLinks = document.querySelectorAll('.side-menu-link');
    console.log(sideMenuLinks);

    sideMenuLinks.forEach(function(link) {
        link.addEventListener('click', function(event) {
            event.preventDefault();  // 기본 동작(페이지 이동)을 막음
            const url = this.getAttribute('data-url');  // 클릭된 링크의 URL을 가져옴
            console.log(url);

            // AJAX 요청 (XMLHttpRequest 사용)
            loadContent(url); // 중복된 AJAX 코드 제거하고 함수로 처리
        });
    });

    // 페이지네이션 링크 클릭 시 (이벤트 위임 방식)
    function setupPaginationLinks() {
        const paginationContainer = document.querySelector('.pagination');

        // 페이지네이션이 존재하지 않으면 종료
        if (!paginationContainer) return;

        // 이미 이벤트 리스너가 등록되었는지 확인 후 등록
        paginationContainer.removeEventListener('click', handlePaginationClick); // 기존 리스너 제거
        paginationContainer.addEventListener('click', handlePaginationClick); // 새로운 리스너 추가
    }

    // 페이지네이션 클릭 시 처리
    function handlePaginationClick(event) {
        if (event.target && event.target.tagName === 'A') {
            event.preventDefault();
            let url = event.target.getAttribute('data-url');
            console.log(url);

        // 페이지네이션에서 isPending 상태를 URL에 포함
        if (isPending === 'true') {
            // 'pending=true'를 URL에 추가
            const urlObj = new URL(url, window.location.origin);  // 현재 도메인을 기반으로 URL 객체 생성
            urlObj.searchParams.set('pending', 'true');  // 'pending' 파라미터 추가
            url = urlObj.toString();  // URL을 다시 문자열로 변환
            console.log(url);
        }
            
            loadContent(url);  // 페이지네이션 클릭시도 loadContent 함수로 처리
        }
    }

    document.body.addEventListener('change', function(event) {
        if (event.target && event.target.id === 'reportCategory') {
            const category = event.target.value;  // 선택된 카테고리 값
            console.log(category);

            // 선택된 카테고리를 드롭다운에서 "selected"로 설정
            document.getElementById('reportCategory').value = category;  // 선택된 카테고리 값을 드롭다운에 반영

            if (category === '') {  // '모든 신고 사유'가 선택되었을 때
                loadContent('/user/reportList?pageNo=1&qty=10');  // 전체 리스트 불러오기
            } else {
                filterReports(category);  // '모든 신고 사유'가 아닐 경우 필터링
            }
        }
    });


   // 신고 필터링 함수 (사유별 필터링)
    function filterReports(selectedCategory) {
        console.log(selectedCategory);
        // const category = document.getElementById('reportCategory').value;  // 선택된 카테고리 값
        // console.log(category);
        const url = `/user/reportList?reportCompId=${selectedCategory}&pageNo=1&qty=10`;

        // AJAX 요청을 통해 필터링된 데이터 받아오기
        loadContent(url);
    }


    // "답변대기만 보기" 버튼 클릭 시 처리 (이벤트 위임)
    document.body.addEventListener('click', function(event) {
        if (event.target && event.target.id === 'viewPendingBtn') {
            isPending = 'true';  // '답변대기만 보기' 상태로 설정
            loadContent('/qna/list?pageNo=1&qty=10&pending=true');  // 'pending=true'로 콘텐츠 요청
        }
    });

    // "전체보기" 버튼 클릭 시 처리 (이벤트 위임)
    document.body.addEventListener('click', function(event) {
        if (event.target && event.target.id === 'viewAllBtn') {
            isPending = 'false';  // '전체보기' 상태로 설정
            loadContent('/qna/list?pageNo=1&qty=10');  // 'pending' 파라미터 없이 콘텐츠 요청
        }
    });

    // "추방" 버튼 클릭 시 처리 (이벤트 위임)
    document.body.addEventListener('click', function(event) {
        if (event.target && event.target.classList.contains('suspend-btn')) {
            const userId = event.target.getAttribute('data-user-id');  // 클릭된 버튼의 사용자 ID
            console.log('User ID to ban:', userId);

            // 확인 메시지 표시
            if (confirm('정말로 이 사용자를 추방하시겠습니까?')) {
                suspendUser(userId);  // 사용자 ID를 전달하여 추방 처리
            }
        }
    });

    // 추방 처리 함수
    function suspendUser(userId) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', `/user/ban?userNum=${userId}`, true);  // 추방 요청 보내기
        xhr.onload = function() {
            if (xhr.status === 200) {
                alert('사용자가 추방되었습니다.');
                location.reload();  // 페이지 새로 고침하여 변경 사항 반영
            } else {
                alert('추방에 실패했습니다. 다시 시도해 주세요.');
            }
        };
        xhr.send();
    }


    // 콘텐츠 및 페이지네이션을 처리하는 함수
    function loadContent(url) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.onload = function() {
            if (xhr.status === 200) {
                const parser = new DOMParser();
                const doc = parser.parseFromString(xhr.responseText, 'text/html');
                const content = doc.querySelector('#content');
                const pagination = doc.querySelector('.pagination');
                const category = doc.querySelector('#reportCategory');
                console.log(category);

                // 새로운 콘텐츠를 #contentArea에 삽입
                if (content) {
                    document.getElementById('contentArea').innerHTML = content.innerHTML;
                }

                // 페이지네이션을 갱신
                if (pagination) {
                    document.querySelector('.pagination').innerHTML = pagination.innerHTML;
                }

               // 서버에서 받은 카테고리 값에 맞게 드롭다운 설정
               if (category) {
                const selectedValue = category.value || '';  // 서버에서 받은 카테고리 값
                console.log(selectedValue);  // selectedValue 값 확인
                document.getElementById('reportCategory').value = selectedValue; // 선택된 카테고리 값 설정
             }

                // 페이지네이션 링크 클릭 처리
                setupPaginationLinks(); // 페이지네이션 링크를 다시 설정

                // 콘텐츠 로드 후 버튼 상태 갱신
                updateButtonState(); // 전역 isPending 값에 따라 버튼 상태를 갱신
            }
        };
        xhr.send();
    }

    // 버튼 상태 변경 함수
    function updateButtonState() {
        if (isPending === 'true') {
            document.getElementById('viewPendingBtn').style.display = 'none';  // "답변대기만 보기" 버튼 숨기기
            document.getElementById('viewAllBtn').style.display = 'inline-block';  // "전체보기" 버튼 보이기
        } else {
            document.getElementById('viewPendingBtn').style.display = 'inline-block';  // "답변대기만 보기" 버튼 보이기
            document.getElementById('viewAllBtn').style.display = 'none';  // "전체보기" 버튼 숨기기
        }
    }



    // 검색 폼 제출 시 처리
    window.searchUsers = function(event) {
        event.preventDefault();  // 폼의 기본 제출을 방지
        const keyword = document.getElementById('page-search-box').value.trim();
        const searchUrl = `/user/list?keyword=${encodeURIComponent(keyword)}`;
        console.log(keyword);
        console.log(searchUrl);
        loadContent(searchUrl);  // 검색 결과를 동적으로 불러오기
    };

});

