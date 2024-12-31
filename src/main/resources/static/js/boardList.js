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

// 페이지 로드 시 URL 파라미터로 'includeSoldOut' 값 설정
window.addEventListener('DOMContentLoaded', function() {
  const urlParams = new URLSearchParams(window.location.search);
  const includeSoldOutParam = urlParams.get('includeSoldOut'); // URL에서 'includeSoldOut' 파라미터 추출
  var includeSoldOutInput = document.getElementById("includeSoldOut");
  var iconUnchecked = document.getElementById("icon-unchecked");
  var iconChecked = document.getElementById("icon-checked");

  if (includeSoldOutParam === "true") {
      includeSoldOutInput.value = "true";
      iconUnchecked.style.display = "none";
      iconChecked.style.display = "inline";
  } else {
      includeSoldOutInput.value = "false";
      iconUnchecked.style.display = "inline";
      iconChecked.style.display = "none";
  }
});


// 페이지 로드 시 URL에서 필터 파라미터 읽어오기
window.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    
    // 선택한 필터 항목 표시
    function addFilterToSelectedList(filterDescription, filterKey, filterValue) {
        const filterList = document.querySelector('.selected-filters'); // '선택한 필터' 영역
        const filterItem = document.createElement('li');
        
        filterItem.classList.add('filter-item');
        filterItem.innerHTML = `${filterDescription} <button class="remove-filter" data-filter-key="${filterKey}" data-filter-value="${filterValue}">X</button>`;
        
        filterList.appendChild(filterItem);
    }

    // URL에서 필터 값 추출하여 표시
    const category = urlParams.get('category');
    const minPrice = urlParams.get('minPrice');
    const maxPrice = urlParams.get('maxPrice');
    const includeSoldOut = urlParams.get('includeSoldOut');

    // 카테고리 필터 추가
    if (category) {
        addFilterToSelectedList(`카테고리: ${category}`, 'category', category);
    }

    // 가격 필터 추가
    if (minPrice && maxPrice) {
        addFilterToSelectedList(`가격: ${minPrice} ~ ${maxPrice}`, 'price', `${minPrice}-${maxPrice}`);
    }

    // '판매완료 상품 포함' 필터만 추가
    if (includeSoldOut !== null) {
      if (includeSoldOut === 'true') {
          addFilterToSelectedList('판매완료 상품 포함', 'includeSoldOut', 'true');
      }
  }

    // X 버튼 클릭 시 해당 필터만 제거
    document.querySelector('.selected-filters').addEventListener('click', function(event) {
      if (event.target.classList.contains('remove-filter')) {
        const filterKey = event.target.getAttribute('data-filter-key');
        const filterValue = event.target.getAttribute('data-filter-value');
    
        // 필터 항목을 URL에서 제거
        if (filterKey === 'price') {
          // 가격 필터일 경우 minPrice와 maxPrice 제거
          urlParams.delete('minPrice');
          urlParams.delete('maxPrice');
        } else {
          urlParams.delete(filterKey);
        }
    
        // 새로 고침된 URL로 이동하여 필터 제거
        const newUrl = `${window.location.pathname}?${urlParams.toString()}`;
        window.location.href = newUrl;  // 페이지 새로 고침
      }
    });
});


document.querySelectorAll('.sort-btn').forEach(button => {
  button.addEventListener('click', function() {
    const sortValue = this.getAttribute('data-sort');  // 클릭한 버튼의 data-sort 값 가져오기
    const urlParams = new URLSearchParams(window.location.search);  // 현재 URL에서 파라미터 가져오기

    // 기존 필터와 페이지를 유지하면서 sort 파라미터만 갱신
    urlParams.set('sort', sortValue);

    // URL 갱신
    window.location.href = `${window.location.pathname}?${urlParams.toString()}`;
  });
});


document.addEventListener("DOMContentLoaded", function() {
  const productList = document.getElementById('productList');
  const productItems = productList.getElementsByClassName('product-item');
  
  // 상품이 하나만 있으면 single-item 클래스 추가
  if (productItems.length === 1) {
      productList.classList.add('single-item');
  }

  // 상품 개수가 5개 이하일 경우 'small-item-count' 클래스 추가
  if (productItems.length <= 5) {
      productList.classList.add('small-item-count');
  } else {
    productList.classList.remove('small-item-count');
  }
});