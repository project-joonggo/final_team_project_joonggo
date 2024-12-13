
    let map;
    let geocoder;

    function initializeMap() {
      if (!sessionStorage.getItem('addressSubmitted')) {
        // addressSubmitted가 없을 경우에만 초기화
        sessionStorage.removeItem('addressSubmitted'); // sessionStorage 초기화
      }

      // Geocoder 객체 생성
      geocoder = new kakao.maps.services.Geocoder();
  
      // 페이지 로드 시 위치 정보 가져오기
      getLocation();
  }

    function getLocation() {
      const status = document.getElementById("status");

      // Geolocation API 지원 여부 확인
      if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            const { latitude, longitude } = position.coords;
            status.textContent = `위도: ${latitude}, 경도: ${longitude}`;

            // Kakao Maps Geocoder로 도로명 주소 가져오기
            const coords = new kakao.maps.LatLng(latitude, longitude);

            // 지도를 해당 위치로 이동
            const mapContainer = document.getElementById('map');
            const mapOption = {
              center: coords, // 지도의 중심 좌표 설정
              level: 7  // 지도 확대 수준(레벨) 설정
            };
            map = new kakao.maps.Map(mapContainer, mapOption);

            // 마커 표시
            const marker = new kakao.maps.Marker({
              position: coords,
              map: map
            });
            // marker.setMap(map);

            // 주소 변환
            geocoder.coord2Address(coords.getLng(), coords.getLat(), (result, status) => {
              console.log("API 호출 결과: ", result);
              console.log("API 상태: ", status);

              if (status === kakao.maps.services.Status.OK) {
                  // console.log("result.length: ", result.length);
                  // console.log("result[0].road_address: ", result[0].road_address);

                  if (result.length > 0) {
                    // 도로명 주소가 있으면 도로명 주소 사용, 없으면 일반 주소 사용
                    const region_1depth_name = result[0]?.address?.region_1depth_name;
                    const region_2depth_name = result[0]?.address?.region_2depth_name;
                    const region_3depth_name = result[0]?.address?.region_3depth_name;
                    const depthAddress = region_1depth_name + " " + region_2depth_name + " " + region_3depth_name;
                    
                    // console.log("roadAddress: ", roadAddress);
                    // console.log("result[0]?.address_name: ", result[0]?.address_name);
                    // console.log("주소: ", address);

                      if (depthAddress) {
                          console.log("도로명 주소: ", depthAddress);

                          // 페이지 로드 후 한 번만 자동 제출하도록 설정
                          const addressInput = document.getElementById("addressInput");
                          addressInput.value = depthAddress;
                          console.log(addressInput.value);

                          // 주소 정보를 sessionStorage에 저장
                          sessionStorage.setItem('userAddress', depthAddress);
                          
                          // 'addressForm' 제출 방지 여부 확인
                          if (!sessionStorage.getItem('addressSubmitted')) {
                            const addressForm = document.getElementById("addressForm");
                            console.log(addressInput.value);
                            addressForm.submit();

                            // 제출한 후 sessionStorage에 표시
                            sessionStorage.setItem('addressSubmitted', 'true');
                          }
                      } else {
                          console.error("도로명 주소를 찾을 수 없습니다.");
                      }
                  } else {
                      console.error("주소 변환에 실패했습니다.");
                  }
              } else {
                  console.log("coord2Address API 호출 실패: ", status);
              }
            });

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

    // Kakao 지도 API가 로드된 후 initializeMap 호출
    window.onload = initializeMap;

 // kakao map API로 지도 띄우기
/* var container = document.getElementById('map');
       var options = {
           center: new kakao.maps.LatLng(33.450701, 126.570667),
           level: 3
       };

       var map = new kakao.maps.Map(container, options);*/
