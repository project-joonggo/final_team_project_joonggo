
    let map;
    let ps;

    function getLocation() {
      const status = document.getElementById("status");

      // Geolocation API 지원 여부 확인
      if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            const { latitude, longitude, accuracy } = position.coords;
            status.textContent = `위도: ${latitude}, 경도: ${longitude}, 정확도: ${accuracy}미터`;

            // 지도를 해당 위치로 이동
            const mapContainer = document.getElementById('map');
            const mapOption = {
              center: new kakao.maps.LatLng(latitude, longitude), // 지도의 중심 좌표 설정
              level: 7  // 지도 확대 수준(레벨) 설정
            };
            map = new kakao.maps.Map(mapContainer, mapOption);

            // 마커 표시
            const marker = new kakao.maps.Marker({
              position: new kakao.maps.LatLng(latitude, longitude)
            });
            marker.setMap(map);

            // // 장소 검색 객체 생성
            // ps = new kakao.maps.services.Places();

            // // 주변 나들이 장소 검색
            // const placesList = document.getElementById("places");
            // ps.keywordSearch("관광지", (data, status, pagination) => {
            //   if (status === kakao.maps.services.Status.OK) {
            //     placesList.innerHTML = "";
            //     data.forEach((place) => {
            //       const listItem = document.createElement("li");
            //       listItem.textContent = `${place.place_name} (${place.address_name})`;
            //       placesList.appendChild(listItem);

            //       // 지도에 마커 추가
            //       const placeMarker = new kakao.maps.Marker({
            //         position: new kakao.maps.LatLng(place.y, place.x),
            //         map: map
            //       });
            //     });
            //   } else {
            //     placesList.textContent = "주변 관광지를 찾을 수 없습니다.";
            //   }
            // }, {
            //   location: new kakao.maps.LatLng(latitude, longitude),
            //   radius: 5000 // 5km 내에서 검색
            // });
          },
          (error) => {
            status.textContent = `위치 정보를 가져올 수 없습니다: ${error.message}`;
          },
          {
            enableHighAccuracy: true, // 정확도 우선 모드
            timeout: 10000,           // 10초 이내에 응답 없으면 에러 발생
            maximumAge: 0             // 항상 최신 위치 정보 수집
          }
        );
      } else {
        status.textContent = "브라우저가 위치 서비스를 지원하지 않습니다.";
      }
    }

 // kakao map API로 지도 띄우기
/* var container = document.getElementById('map');
       var options = {
           center: new kakao.maps.LatLng(33.450701, 126.570667),
           level: 3
       };

       var map = new kakao.maps.Map(container, options);*/
