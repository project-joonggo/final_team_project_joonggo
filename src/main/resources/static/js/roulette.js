const $c = document.querySelector("canvas");
const ctx = $c.getContext(`2d`);


// const product = [
//   "당첨1", '꽝', "꽝", "당첨2", "꽝", "꽝", '당첨3', "꽝", "꽝",
// ];
const product = [
  "당첨1", '꽝', "당첨2", "꽝", '당첨3', "꽝"
];

// const colors = ["#dc0936", "#e6471d", "#f7a416", "#efe61f ", "#60b236", "#209b6c", "#169ed8", "#3f297e", "#87207b", "#be107f", "#e7167b"];
const colors = ["#dc0936", "#e6471d", "#f7a416", "#efe61f ", "#60b236", "#209b6c", "#169ed8", "#3f297e"];
// const colors = ["#FFFFFF", "#dc0936", "#FFFFFF", "#efe61f", "#FFFFFF", "#169ed8", "#be107f", "#e7167b"];

const newMake = () => {
    const [cw, ch] = [$c.width / 2, $c.height / 2];
    const arc = Math.PI / (product.length / 2);
  
    for (let i = 0; i < product.length; i++) {
      ctx.beginPath();
      ctx.fillStyle = colors[i % (colors.length -1)];
      ctx.moveTo(cw, ch);
      ctx.arc(cw, ch, cw, arc * (i - 1), arc * i);
      ctx.fill();
      ctx.closePath();
    }

    // ctx.beginPath();
    // ctx.strokeStyle = "#000";
    // ctx.lineWidth = 5; 
    // ctx.arc(cw, ch, cw, 0, Math.PI * 2);
    // ctx.stroke();
    // ctx.closePath();


    ctx.fillStyle = "#000";
    ctx.font = "24px Pretendard Variable";
    ctx.textAlign = "center";

    for (let i = 0; i < product.length; i++) {
      const angle = (arc * i) + (arc / 2);

      ctx.save();

      ctx.translate(
        cw + Math.cos(angle) * (cw - 50),
        ch + Math.sin(angle) * (ch - 50),
      );

      ctx.rotate(angle + Math.PI / 2);

      product[i].split(" ").forEach((text, j) => {
        ctx.fillText(text, 0, 30 * j);
      });

      ctx.restore();
    }
}

const rotate = () => {
    $c.style.transform = `initial`;
    $c.style.transition = `initial`;

    setTimeout(() => {
        const ran = Math.floor(Math.random() * product.length);
        const arc = 360 / product.length;
        const rotate = (ran * arc) + 3600 + (arc * 3) - (arc / 4);

        $c.style.transform = `rotate(-${rotate}deg)`;
        $c.style.transition = `2s`;

        setTimeout(() => {
            const result = product[ran];
            alert(result === "꽝" ? `${result}! 아쉽지만 다음 기회에...` : `${result} 축하드립니다!`);

            // 서버로 결과 전송
            fetch("/event/roulette", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ result }), // 결과 전송
            })
                .then((res) => res.json())
                .then((data) => {
                    if (data.success) {
                        console.log("DB 기록 성공:", data.message);
                    } else {
                        console.error("DB 기록 실패:", data.error);
                    }
                })
                .catch((err) => console.error("서버 오류:", err));
        }, 2000);
    }, 1);
};

newMake();