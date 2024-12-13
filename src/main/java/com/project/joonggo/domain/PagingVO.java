package com.project.joonggo.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PagingVO {
    private int pageNo; // 현재 페이지번호
    private int qty; // 한페이지에 출력되는 게시글 개수\
    private String category;
    private String keyword;

    public PagingVO(){
        pageNo = 1;
        qty = 10;
    }

    public PagingVO(int pageNo, int qty) {
        this.pageNo = pageNo;
        this.qty = qty;
    }

    public int getStartIndex(){
        return (this.pageNo-1)*this.qty;
    }
}
