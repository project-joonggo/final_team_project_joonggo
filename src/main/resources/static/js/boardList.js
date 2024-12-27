// 카테고리 토글 버튼을 클릭하면 카테고리 목록을 표시하거나 숨기기
document.getElementById('category-toggle').addEventListener('click', function() {
    const categoryList = document.querySelector('.category-list');
    categoryList.classList.toggle('hidden'); // 목록을 표시하거나 숨기기
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