package com.project.joonggo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaqController {
    @GetMapping("/faq/first")
    public String showFaqFirst() {
        return "/faq/first"; // first.html 파일을 반환
    }

    @GetMapping("/faq/second")
    public String showFaqSecond() {
        return "/faq/second"; // second.html 파일을 반환
    }

    @GetMapping("/faq/third")
    public String showFaqThird() {
        return "/faq/third"; // third.html 파일을 반환
    }

    @GetMapping("/faq/fourth")
    public String showFaqFourth() {
        return "/faq/fourth"; // fourth.html 파일을 반환
    }

    @GetMapping("/faq/fifth")
    public String showFaqFifth() {
        return "/faq/fifth"; // fifth.html 파일을 반환
    }

    // Policy Pages
    @GetMapping("/policy/first")
    public String showPolicyFirst() {
        return "/policy/first"; // first.html 반환
    }

    @GetMapping("/policy/second")
    public String showPolicySecond() {
        return "/policy/second"; // second.html 반환
    }

    @GetMapping("/policy/third")
    public String showPolicyThird() {
        return "/policy/third"; // third.html 반환
    }

    @GetMapping("/policy/fourth")
    public String showPolicyFourth() {
        return "/policy/fourth"; // fourth.html 반환
    }

    @GetMapping("/policy/fifth")
    public String showPolicyFifth() {
        return "/policy/fifth"; // fifth.html 반환
    }

    @GetMapping("/policy/sixth")
    public String showPolicySixth() {
        return "/policy/sixth"; // sixth.html 반환
    }
}
