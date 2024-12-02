package com.project.joonggo.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment/*")
@Slf4j
public class PaymentController {


    @GetMapping("/buy")
    public String buy(){

        return "/payment/buy";
    }
}
