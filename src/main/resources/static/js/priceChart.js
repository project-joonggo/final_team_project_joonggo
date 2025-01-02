console.log("chart js ~ in !!!");

document.addEventListener("DOMContentLoaded", function() {
    var ctx = document.getElementById('priceChart').getContext('2d');
    
    // averagePriceData가 JSON 문자열이라면 파싱
    if (typeof averagePriceData === 'string') {
        averagePriceData = JSON.parse(averagePriceData);
    }

    console.log(Array.isArray(averagePriceData)); // true여야 합니다
    console.log(averagePriceData);  // 실제 데이터 확인
    
    if (Array.isArray(averagePriceData)) {
        // 날짜와 평균 가격을 각각 추출
        var labels = averagePriceData.map(function(item) {
            var date = new Date(item.date);  // 밀리초 단위의 날짜 값을 JavaScript Date 객체로 변환
            return date.toLocaleDateString();  // 날짜를 "yyyy-mm-dd" 형식으로 변환
        });

        var data = averagePriceData.map(function(item) {
            return item.avg_price;  // 평균 가격을 추출
        });

        // 차트 생성
        new Chart(ctx, {
            type: 'line',  // 막대 그래프
            data: {
                labels: labels,  // 날짜를 X축 레이블로 사용
                datasets: [{
                    label: '가격',
                    data: data,  // 평균 가격을 데이터로 사용
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',  // 바 색상
                    borderColor: 'rgba(75, 192, 192, 1)',  // 바 경계 색상
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {  // y축에 콤마를 추가하여 숫자 포맷
                                return value.toLocaleString();
                            }
                        }
                    }
                }
            }
        });
    } else {
        console.error("averagePriceData는 배열이 아닙니다.");
    }
});