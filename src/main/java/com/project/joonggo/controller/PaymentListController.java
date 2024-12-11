package com.project.joonggo.controller;

import com.project.joonggo.domain.Payment;
import com.project.joonggo.service.LoginService;
import com.project.joonggo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentListController {
    private final PaymentService paymentService;
    private final LoginService loginService;

    @GetMapping("/history")
    public String paymentHistory(Model model, Principal principal) {
        // 로그인한 사용자의 usernum을 가져오는 방법
        String userId = principal.getName(); // 로그인된 사용자 아이디

        Long userNum = loginService.getUsernumByUserId(userId);  // userId로 usernum 조회

        log.info(">>>> userNum >> {}" , userNum);


        // usernum을 이용해 결제 내역 조회
        List<Payment> payments = paymentService.getPaymentHistory(userNum);

        // 결제 내역을 모델에 추가
        model.addAttribute("payments", payments);

        // 결제 내역 페이지 반환
        return "/payment/history";
    }
}
