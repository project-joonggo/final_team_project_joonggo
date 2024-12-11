document.querySelectorAll('.remove-wish-btn').forEach(button => {
    button.addEventListener('click', function() {
        const boardId = button.getAttribute('data-board-id');

        // 찜 취소 요청을 서버로 전송
        fetch(`/wish/removeFromWishList`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ boardId: boardId })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // 찜 항목이 성공적으로 삭제되면 화면에서 해당 항목을 삭제
                button.closest('tr').remove();
                alert(data.message);  // 삭제된 후 알림
            } else {
                alert("찜 취소에 실패했습니다.");
            }
        })
        .catch(error => console.error('Error:', error));
    });
});
