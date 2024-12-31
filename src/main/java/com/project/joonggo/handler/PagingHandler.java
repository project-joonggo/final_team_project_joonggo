package com.project.joonggo.handler;

import com.project.joonggo.domain.PagingVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class PagingHandler {

    private int startPage;
    private int endPage;
    private int realEndPage;
    private boolean prev, next;

    private int totalCount;
    private PagingVO pgvo;

    public PagingHandler(PagingVO pgvo, int totalCount){
        this.pgvo = pgvo;
        this.totalCount = totalCount;

        if (totalCount == 0) {
            this.startPage = 1;
            this.endPage = 1;
            this.realEndPage = 1; // 실제 끝 페이지를 1로 설정
            this.prev = false;
            this.next = false;
            return; // 더 이상 진행하지 않고 종료
        }


        // 페이지 블록 크기 설정 (보통 10개씩 묶어서 표시)
        int pageBlock = 10;

        // endPage 계산: pageBlock 기준으로 페이지 블록의 끝 계산
        this.endPage = (int) Math.ceil(pgvo.getPageNo() / (double) pageBlock) * pageBlock;
        this.startPage = endPage - (pageBlock - 1);

        this.realEndPage = (int)Math.ceil(totalCount / (double)pgvo.getQty());

        if(realEndPage < endPage){
            this.endPage = realEndPage;
        }

        this.prev = this.startPage > 1;  // 1 11 21
        this.next = this.endPage < realEndPage;

    }

}
