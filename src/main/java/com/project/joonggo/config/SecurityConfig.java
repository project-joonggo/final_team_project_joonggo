package com.project.joonggo.config;

import com.project.joonggo.security.CustomUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize)-> authorize
                                .requestMatchers(
                                        "/", "/js/**", "/img/**", "/css/**", "/dist/**", "/upload/**",
                                        "/index", "/user/join", "/user/login", "/board/list", "/board/detail/**",
                                        "/comment/list/**", "/smarteditor/**", "/user/kakao/**", "/user/google/**",
                                        "/user/naver/**", "/user/phoneCheck", "/user/findId", "/user/findIdResult").permitAll()
    /*                    .requestMatchers("/**").permitAll()*/
                                .requestMatchers("/ws/**", "/notifications/**","/notice/**").permitAll() // WebSocket 경로 허용
                                .requestMatchers("/user/list").hasAnyRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/board/list")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login") // 로그인 페이지 설정
                        .defaultSuccessUrl("/board/list", true) // 로그인 성공 후 리다이렉트 URL 설정
                        .failureUrl("/user/login?error=true") // 로그인 실패 후 리다이렉트 URL 설정
                )
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .invalidateHttpSession(false)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/")
                )
                .headers(headers -> headers
                        // CSP 설정: frame-ancestors를 사용하여 iframe에 대한 제어
                        .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self' http://localhost:8089"))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // 세션을 항상 생성하도록 설정
                        .sessionFixation().none() // 세션 고정 방어를 하지 않음
                )
                .build();

    }

    @Bean
    UserDetailsService customUserService(){
        return new CustomUserService();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public OAuth2UserService oAuth2UserService() {
        return new DefaultOAuth2UserService();
    }




}