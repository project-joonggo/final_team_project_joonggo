// 슬라이드 3개
const slide3 = new Swiper('#slide3', {

	slidesPerView : 3, // 동시에 보여줄 슬라이드 갯수
	spaceBetween : 30, // 슬라이드간 간격
	slidesPerGroup : 3, // 그룹으로 묶을 수, slidesPerView 와 같은 값을 지정하는게 좋음

	// 그룹수가 맞지 않을 경우 빈칸으로 메우기
	// 3개가 나와야 되는데 1개만 있다면 2개는 빈칸으로 채워서 3개를 만듬
	loopFillGroupWithBlank : true,

	loop : true, // 무한 반복
	
	autoplay: {
		delay: 5000, // 3초마다 자동으로 슬라이드가 넘어갑니다.
		disableOnInteraction: false, // 사용자가 슬라이드를 클릭해도 자동 슬라이드가 멈추지 않도록 설정
	},

	// ★동적로딩 설정
	lazy : {
		loadPrevNext : true // 이전, 다음 이미지는 미리 로딩
	},

	pagination : { // 페이징
		el : '#slide3 .swiper-pagination',
		clickable : true, // 페이징을 클릭하면 해당 영역으로 이동, 필요시 지정해 줘야 기능 작동
	},
	navigation : { // 네비게이션
		nextEl : '#slide3 .swiper-button-next', // 다음 버튼 클래스명
		prevEl : '#slide3 .swiper-button-prev', // 이번 버튼 클래스명
	},
});

// 슬라이드 6개_1
const slide6_1 = new Swiper('#slide6_1', {

	slidesPerView : 6, // 동시에 보여줄 슬라이드 갯수
	spaceBetween : 30, // 슬라이드간 간격
	slidesPerGroup : 6, // 그룹으로 묶을 수, slidesPerView 와 같은 값을 지정하는게 좋음

	// 그룹수가 맞지 않을 경우 빈칸으로 메우기
	// 3개가 나와야 되는데 1개만 있다면 2개는 빈칸으로 채워서 3개를 만듬
	loopFillGroupWithBlank : true,

	loop : true, // 무한 반복

	pagination : { // 페이징
		el : '#slide6_1 .swiper-pagination',
		clickable : true, // 페이징을 클릭하면 해당 영역으로 이동, 필요시 지정해 줘야 기능 작동
	},
	navigation : { // 네비게이션
		nextEl : '#slide6_1 .swiper-button-next', // 다음 버튼 클래스명
		prevEl : '#slide6_1 .swiper-button-prev', // 이번 버튼 클래스명
	},
});

// 슬라이드 6개_2
const slide6_2 = new Swiper('#slide6_2', {

	slidesPerView : 6, // 동시에 보여줄 슬라이드 갯수
	spaceBetween : 30, // 슬라이드간 간격
	slidesPerGroup : 6, // 그룹으로 묶을 수, slidesPerView 와 같은 값을 지정하는게 좋음

	// 그룹수가 맞지 않을 경우 빈칸으로 메우기
	// 3개가 나와야 되는데 1개만 있다면 2개는 빈칸으로 채워서 3개를 만듬
	loopFillGroupWithBlank : true,

	loop : true, // 무한 반복

	pagination : { // 페이징
		el : '#slide6_2 .swiper-pagination',
		clickable : true, // 페이징을 클릭하면 해당 영역으로 이동, 필요시 지정해 줘야 기능 작동
	},
	navigation : { // 네비게이션
		nextEl : '#slide6_2 .swiper-button-next', // 다음 버튼 클래스명
		prevEl : '#slide6_2 .swiper-button-prev', // 이번 버튼 클래스명
	},
});

// 슬라이드 6개_3
const slide6_3 = new Swiper('#slide6_3', {

	slidesPerView : 6, // 동시에 보여줄 슬라이드 갯수
	spaceBetween : 30, // 슬라이드간 간격
	slidesPerGroup : 6, // 그룹으로 묶을 수, slidesPerView 와 같은 값을 지정하는게 좋음

	// 그룹수가 맞지 않을 경우 빈칸으로 메우기
	// 3개가 나와야 되는데 1개만 있다면 2개는 빈칸으로 채워서 3개를 만듬
	loopFillGroupWithBlank : true,

	loop : true, // 무한 반복

	pagination : { // 페이징
		el : '#slide6_3 .swiper-pagination',
		clickable : true, // 페이징을 클릭하면 해당 영역으로 이동, 필요시 지정해 줘야 기능 작동
	},
	navigation : { // 네비게이션
		nextEl : '#slide6_3 .swiper-button-next', // 다음 버튼 클래스명
		prevEl : '#slide6_3 .swiper-button-prev', // 이번 버튼 클래스명
	},
});