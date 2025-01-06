package com.project.joonggo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        // 로그인 실패 시 메시지 설정
        String errorMessage = "아이디 또는 비밀번호가 잘못되었습니다."; // 기본 오류 메시지

        // 예를 들어, 로그인 실패 이유에 따라 다르게 메시지 처리
        if (exception.getMessage().contains("Bad credentials")) {
            errorMessage = "잘못된 자격 증명입니다.";
        }

        // 한글 메시지 인코딩
        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // URL 파라미터로 에러 메시지 전달
        String redirectUrl = "/user/login?error=true&errorMessage=" + encodedErrorMessage;

        // 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}
