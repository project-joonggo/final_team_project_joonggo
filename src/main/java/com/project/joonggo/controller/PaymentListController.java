package com.project.joonggo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.joonggo.domain.Payment;
import com.project.joonggo.domain.PaymentDTO;
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

    @GetMapping("/history")
    public String paymentHistory(Model model, Principal principal) {

        Long userNum = Long.valueOf(principal.getName());

        log.info(">>> userNum >> {}", userNum);


        // usernum을 이용해 결제 내역 조회
        List<PaymentDTO> payments = paymentService.getPaymentHistoryWithImages(userNum);

        // 결제 내역을 모델에 추가
        model.addAttribute("payments", payments);


        // 결제 내역 페이지 반환
        return "/payment/history";
    }
}
