    // 클릭 시 현재 사이트의 url 복사
    function clip(){
        var url = '';
        var textarea = document.createElement("textarea");
        document.body.appendChild(textarea);
        url = window.location.href;
        textarea.value = url;
        textarea.select();
        document.execCommand("copy");
        document.body.removeChild(textarea);
        showAlert("링크가 복사되었습니다.")
    };
