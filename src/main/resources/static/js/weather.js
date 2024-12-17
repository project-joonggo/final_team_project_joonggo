function initializeMap() {
  if (!sessionStorage.getItem('addressSubmitted')) {
      if (parsedAddress) {
          // 페이지 로드 후 한 번만 자동 제출하도록 설정
          const addressInput = document.getElementById("addressInput");
          addressInput.value = parsedAddress;

          // 주소를 URL의 쿼리 파라미터로 전달
          const urlWithParams = window.location.href.split('?')[0] + '?address=' + encodeURIComponent(parsedAddress);

          // AJAX로 주소를 서버로 보냄 (GET 방식으로 쿼리 파라미터로 전달)
          fetch(urlWithParams, {
              method: 'GET'
          })
          .then(response => response.text()) // 서버 응답 처리
          .then(data => {
              // 서버로부터 응답받은 후 처리
              console.log("Response:", data);
              sessionStorage.setItem('addressSubmitted', 'true');
          })
          .catch(error => console.error('Error:', error));
      }
  }
}

// 기존 방식
// URL 경로 자동 변경 문제(새 탭마다 로그인한 index에서 다른 경로로 이동 시 index.html로 한 번만 이동하는 문제 있음)

// document.addEventListener('DOMContentLoaded', function () {
//   console.log("Parsed Address:", parsedAddress);
//   // 페이지가 로드되면 initializeMap 함수 호출
//   initializeMap();
// });


// function initializeMap() {
//   if (!sessionStorage.getItem('addressSubmitted')) {
//     // addressSubmitted가 없을 경우에만 초기화
//    //  sessionStorage.removeItem('addressSubmitted'); // sessionStorage 초기화
//     if (parsedAddress) {
      
//       // 페이지 로드 후 한 번만 자동 제출하도록 설정
//       const addressInput = document.getElementById("addressInput");
//       addressInput.value = parsedAddress;
      
//       // 'addressForm' 제출 방지 여부 확인
//      //  if (!sessionStorage.getItem('addressSubmitted')) {
//         const addressForm = document.getElementById("addressForm");
//         console.log(addressInput.value);
//         addressForm.submit();
        
//         // 제출한 후 sessionStorage에 표시
//         sessionStorage.setItem('addressSubmitted', 'true');
//        // }
//      }
//    }
// }

// document.addEventListener('DOMContentLoaded', function () {
//  console.log("Parsed Address:", parsedAddress);
//  // 로드된 후 initializeMap 호출
//  window.onload = initializeMap;
// });