package com.project.joonggo.controller;

import com.project.joonggo.domain.UserVO;
import com.project.joonggo.handler.PhoneAuthHandler;
import com.project.joonggo.handler.SocialLoginHandler;
import com.project.joonggo.repository.UserMapper;
import com.project.joonggo.security.AuthUser;
import com.project.joonggo.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequestMapping("/user/*")
@RequiredArgsConstructor
@Controller
public class LoginController {
    private final LoginService loginService;
    private final PasswordEncoder passwordEncoder;
    private final PhoneAuthHandler phoneAuthHandler;
    private final SocialLoginHandler socialLoginHandler;
    private final UserMapper userMapper;

    private static final int SIGN_FLAG_DEFAULT = 0;
    private static final int SIGN_FLAG_KAKAO = 1;
    private static final int SIGN_FLAG_NAVER = 2;
    private static final int SIGN_FLAG_GOOGLE = 3;

    //회원가입 페이지 이동
    @GetMapping("/join")
    public String join(Model model){
        // 기본 유저의 소셜가입구분을 0으로 (0:기본값, 1:카카오, 2:네이버, 3:구글)
        model.addAttribute("signflag", SIGN_FLAG_DEFAULT);
        return "/user/join";
    }

    //회원가입 요청
    @PostMapping("/join")
    public String join(UserVO userVO){
        userVO.setPassword(passwordEncoder.encode(userVO.getPassword()));
        int isOk = loginService.insert(userVO);
        return "/user/login";
    }

    //로그인 페이지 이동
    @GetMapping("/login")
    public String login(Model model) {
        String kakaoLoginPageUrl = socialLoginHandler.getKakaoLoginPageUrl();
        String naverLoginPageUrl = socialLoginHandler.getNaverLoginPageUrl();
        String googleLoginPageUrl = socialLoginHandler.getGoogleLoginPageUrl();

        model.addAttribute("kakaoLoginPageUrl", kakaoLoginPageUrl);
        model.addAttribute("naverLoginPageUrl", naverLoginPageUrl);
        model.addAttribute("googleLoginPageUrl", googleLoginPageUrl);

        return "/user/login";
    }

    //휴대폰 인증
    @ResponseBody
    @GetMapping("/phoneCheck")
    // 휴대폰 인증번호
    public String sendSMS(String phone){ // 휴대폰 문자보내기
        //난수 생성
        int ranNum = (int)((Math.random()*(9999-1000+1)) + 1000);
        phoneAuthHandler.certifiedPhoneNumber(phone, ranNum);
        return Integer.toString(ranNum);
    }

    //////////////////////////
    /* 구글 소셜 로그인 line */
    ////////////////////////
    @GetMapping("/google/callback")
    public String googleCallback(@RequestParam("code") String authorizeCode, HttpSession session, Model model){
        log.info("===================구글 인가코드========={}", authorizeCode);

        String accessToken = socialLoginHandler.getGoogleAccessToken(authorizeCode);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 구글 액세스 토큰 >>>>>>>>>>>>>>>>>> {}", accessToken);

        String googleUserInfo = socialLoginHandler.getGoogleUserInfo(accessToken);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 구글유저 정보 >>>>>{}", googleUserInfo);

        Map<String, String> userInfo = socialLoginHandler.parseGoogleUserInfo(googleUserInfo);
        String googleId = userInfo.get("userId");
        String googleName = userInfo.get("userName");
        String givenName = userInfo.get("givenName");
        String familyName = userInfo.get("familyName");
        String picture = userInfo.get("picture");
        String email = userInfo.get("email");
        String emailVerified = userInfo.get("emailVerified");

        UserVO loginUser = loginService.findUserByIdAndSignFlag(googleId, SIGN_FLAG_GOOGLE);

        System.out.println("----------------------------"+googleId);
        System.out.println("----------------------------"+SIGN_FLAG_GOOGLE);
        System.out.println("----------------------------"+loginUser);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            loginUser.setAuthList(userMapper.selectAuths(loginUser.getUserNum()));  // 권한을 추가하는 부분

            AuthUser authUser = new AuthUser(loginUser);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            log.info("구글 소셜 로그인 성공 (시큐리티 인증): {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", email);
            model.addAttribute("signflag", SIGN_FLAG_GOOGLE);
            model.addAttribute("socialUserId", googleId);
            return "/user/join";
        }

        return "redirect:/";
    }

    //////////////////////////
    /* 네이버 소셜 로그인 line */
    /////////////////////////
    @GetMapping("/naver/callback")
    public String naverCallback(@RequestParam("code") String authorizeCode, @RequestParam("state") String authorizeState, HttpSession session, Model model){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 인가코드 >>>>> {}", authorizeCode);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 state >>>>>> {}", authorizeState);

        String accessToken = socialLoginHandler.getNaverAccessToken(authorizeCode, authorizeState);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 액세스 토큰 >>>>>>>>>>>>>>>>>> {}", accessToken);

        String naverUserInfo = socialLoginHandler.getNaverUserInfo(accessToken);
        Map<String, String> parseNaverUserInfoResult =  socialLoginHandler.parseNaverUserInfo(naverUserInfo);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 유저 정보 >>>>>{}", parseNaverUserInfoResult);

        String naverId = parseNaverUserInfoResult.get("userId");
        String nickName = parseNaverUserInfoResult.get("nickname");
        String email = parseNaverUserInfoResult.get("email");
        String birthYear = parseNaverUserInfoResult.get("birthYear");
        String birthMonth = parseNaverUserInfoResult.get("birthMonth");
        String birthDay = parseNaverUserInfoResult.get("birthDay");

        UserVO loginUser = loginService.findUserByIdAndSignFlag(naverId, SIGN_FLAG_NAVER);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            loginUser.setAuthList(userMapper.selectAuths(loginUser.getUserNum()));  // 권한을 추가하는 부분

            AuthUser authUser = new AuthUser(loginUser);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            log.info("네이버 소셜 로그인 성공 (시큐리티 인증): {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", email);
            model.addAttribute("signflag", SIGN_FLAG_NAVER);
            model.addAttribute("socialUserId", naverId);
            model.addAttribute("birthYear", birthYear);
            model.addAttribute("birthMonth", birthMonth);
            model.addAttribute("birthDay", birthDay);
            return "/user/join";
        }

        return "redirect:/";
    }

    //////////////////////////
    /* 카카오 소셜 로그인 line */
    /////////////////////////
    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String authorizeCode, HttpSession session, Model model) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 인가코드 >>>>>>>>>>>>>>>>>> {}", authorizeCode);

        // 인가 코드로 카카오 토큰 요청
        String accessToken = socialLoginHandler.getKakaoAccessToken(authorizeCode);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 액세스 토큰 >>>>>>>>>>>>>>>>>> {}", accessToken);

        // 이후 토큰을 통해 필요한 데이터를 처리
        // 토큰 유효성 검증
        boolean isTokenValid = socialLoginHandler.validateKakaoAccessToken(accessToken);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 토큰 유효성 >>>>>>>>>>>>>>>>>> {}", isTokenValid);

        String kakaoUserInfo = socialLoginHandler.getKakaoUserInfo(accessToken);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 유저 정보 >>>>>{}", kakaoUserInfo);

        Map<String, String> userInfo = socialLoginHandler.parseKakaoUserInfo(kakaoUserInfo);
        log.info("parsekakaoUserInfo>>>>>>>>>>{}", userInfo);

        String kakaoId = userInfo.get("userId");
        String kakaoEmail = userInfo.get("email");

        // 이메일로 회원이 존재하는지 확인
        // DB에서 user_id로 사용자 조회
        UserVO loginUser = loginService.findUserByIdAndSignFlag(kakaoId, SIGN_FLAG_KAKAO);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            loginUser.setAuthList(userMapper.selectAuths(loginUser.getUserNum()));  // 권한을 추가하는 부분

            AuthUser authUser = new AuthUser(loginUser);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            log.info("카카오 소셜 로그인 성공 (시큐리티 인증): {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", kakaoEmail);
            model.addAttribute("signflag", SIGN_FLAG_KAKAO);
            model.addAttribute("socialUserId", kakaoId);
            return "/user/join";
        }

        return "redirect:/";
    }

}
