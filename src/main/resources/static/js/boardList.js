// 카테고리 토글 버튼을 클릭하면 카테고리 목록을 표시하거나 숨기기
document.getElementById('category-toggle').addEventListener('click', function() {
    const categoryList = document.querySelector('.category-list');
    const plusIcon = document.getElementById('plus-icon');
    const minusIcon = document.getElementById('minus-icon');

    categoryList.classList.toggle('hidden'); // 목록을 표시하거나 숨기기

    // 아이콘 전환
    if (categoryList.classList.contains('hidden')) {
      // 목록이 숨겨지면 + 아이콘, - 아이콘 숨김
      plusIcon.style.display = 'inline';
      minusIcon.style.display = 'none';
    } else {
      // 목록이 표시되면 - 아이콘, + 아이콘 숨김
      plusIcon.style.display = 'none';
      minusIcon.style.display = 'inline';
    }

  });
  

// 카테고리 항목을 클릭했을 때 선택된 카테고리를 표시하고 목록 숨기기
const categoryLinks = document.querySelectorAll('.category-list a');
categoryLinks.forEach(function(link) {
  link.addEventListener('click', function(event) {
    event.preventDefault();

    const selectedCategory = event.target.dataset.category;

    // 선택한 카테고리의 링크로 이동
    window.location.href = `/board/list?category=${encodeURIComponent(selectedCategory)}&pageNo=1`;

    // 카테고리 목록 숨기기
    document.querySelector('.category-list').classList.add('hidden');
  });
});

// 페이지 로드 시 URL의 카테고리 파라미터로 breadcrumb 추가하기
window.addEventListener('DOMContentLoaded', function() {
    const breadcrumb = document.getElementById('breadcrumb');
    const urlParams = new URLSearchParams(window.location.search);
    const categoryParam = urlParams.get('category'); // URL에서 'category' 파라미터 추출
    
  
    if (categoryParam && !Array.from(breadcrumb.children).some(item => item.textContent === categoryParam)) {
      const newCategoryItem = document.createElement('li');
      const newCategoryLink = document.createElement('a');
      newCategoryLink.href = `/board/list?category=${encodeURIComponent(categoryParam)}&pageNo=1`;
      newCategoryLink.innerText = categoryParam;
  
      newCategoryItem.appendChild(newCategoryLink);
      breadcrumb.appendChild(newCategoryItem); // breadcrumb에 추가
    }
  });


  document.getElementById("option-toggle").addEventListener("click", function (event) {
    event.preventDefault();
    
    // 버튼 클릭 시 'includeSoldOut' 값을 토글
    var includeSoldOutInput = document.getElementById("includeSoldOut");
    var iconUnchecked = document.getElementById("icon-unchecked");
    var iconChecked = document.getElementById("icon-checked");
    
    if (includeSoldOutInput.value === "false") {
        includeSoldOutInput.value = "true";  // '판매완료 상품 포함' 활성화
        iconUnchecked.style.display = "none";
        iconChecked.style.display = "inline";
    } else {
        includeSoldOutInput.value = "false";  // '판매완료 상품 포함' 비활성화
        iconUnchecked.style.display = "inline";
        iconChecked.style.display = "none";
    }

    // 필터링 폼 제출
    document.getElementById("option-form").submit();
});