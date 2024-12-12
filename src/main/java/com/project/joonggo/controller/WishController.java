package com.project.joonggo.controller;


import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.service.BoardService;
import com.project.joonggo.service.LoginService;
import com.project.joonggo.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wish/*")
@Slf4j
public class WishController {
    private final WishService wishService;
    private final BoardService boardService;


    @PostMapping("/getWish")
    @ResponseBody
    public Map<String, Object> toggleWishList(@RequestBody Map<String, Object> request, Principal principal) {
        // 로그인한 사용자의 userId와 userNum을 가져옴
        Long userNum = Long.valueOf(principal.getName());

        log.info(">>> userNum >> {}", userNum);

        String strBoardId = (String) request.get("boardId");  // String으로 받음
        Long boardId = Long.parseLong(strBoardId);  // String을 Long으로 변환


        log.info(">> boardId >>> {}", boardId);

        // 사용자가 이미 찜한 상품인지 확인
        boolean isAlreadyWished = wishService.isAlreadyWished(userNum, boardId);

        Map<String, Object> response = new HashMap<>();
        if (isAlreadyWished) {
            // 찜 취소: wish_list에서 해당 레코드 삭제
            int removeOk = wishService.removeFromWishList(userNum, boardId);
            // sellbuy_board의 wish_count 감소
            if (removeOk > 0) {
                boardService.decrementWishCount(boardId);  // wish_count 감소
            }
            response.put("success", true);
            response.put("message", "찜이 취소되었습니다.");
            response.put("newButtonText", "찜하기");  // 버튼 텍스트 변경
        } else {
            // 찜 등록: wish_list에 찜 정보 추가
            wishService.addToWishList(userNum, boardId);
            // sellbuy_board의 wish_count 증가
            boardService.incrementWishCount(boardId);  // wish_count 증가
            response.put("success", true);
            response.put("message", "찜이 등록되었습니다.");
            response.put("newButtonText", "찜 취소");  // 버튼 텍스트 변경
        }

        // 현재 찜 수를 가져와서 응답에 추가
        int newLikeCount = boardService.getLikeCount(boardId);
        response.put("newLikeCount", newLikeCount);

        return response;  // JSON 응답 반환
    }

    @GetMapping("/wish/list")
    public String showWishList(Principal principal, Model model) {

        Long userNum = Long.valueOf(principal.getName());

        log.info(">>> userNum >>> {}", userNum);

        // 찜한 상품 정보와 이미지 URL을 가져옵니다.
        List<BoardFileDTO> wishedProducts = wishService.getWishedProducts(userNum);

        // 뷰에 전달할 모델에 찜 리스트 정보 추가
        model.addAttribute("wishedProducts", wishedProducts);

        return "wish/list";  // 찜리스트 페이지로 이동
    }


    @PostMapping("/removeFromWishList")
    @ResponseBody
    public Map<String, Object> removeFromWishList(@RequestBody Map<String, Object> request, Principal principal) {
        // 로그인한 사용자의 userId와 userNum을 가져옴

        Long userNum = Long.valueOf(principal.getName());

        log.info(">>> userNum >>>> {}", userNum);

        String strBoardId = (String) request.get("boardId");  // String으로 받음
        Long boardId = Long.parseLong(strBoardId);  // String을 Long으로 변환

        log.info("boardId >>> {}", boardId);

        // 찜리스트에서 해당 상품을 삭제
        int removeOk = wishService.removeFromWishList(userNum, boardId);

        Map<String, Object> response = new HashMap<>();
        if (removeOk > 0) {
            boardService.decrementWishCount(boardId);  // wish_count 감소
            response.put("success", true);
            response.put("message", "찜이 취소되었습니다.");
        } else {
            response.put("success", false);
            response.put("message", "찜 취소에 실패했습니다.");
        }

        return response;  // JSON 응답 반환
    }


}
