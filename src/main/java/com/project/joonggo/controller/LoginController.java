package com.project.joonggo.controller;

import com.project.joonggo.domain.UserVO;
import com.project.joonggo.handler.SocialLoginHandler;
import com.project.joonggo.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping("/login/*")
@RequiredArgsConstructor
@Controller
public class LoginController {
    private final LoginService loginService;

    private final SocialLoginHandler socialLoginHandler;

    //회원가입 페이지 이동
    @GetMapping("/join")
    public String join(){
        return "/user/join";
    }

    //회원가입 요청
    @PostMapping("/join")
    public String join(UserVO userVO){
        //userVO.setPwd(passwordEncoder.encode(userVO.getPwd()));
        int isOk = loginService.insert(userVO);
        return "/user/login";
    }

    //로그인 페이지 이동
    @GetMapping("/page")
    public String login(Model model) {
        String kakaoLoginPageUrl = socialLoginHandler.getKakaoLoginPageUrl();
        String naverLoginPageUrl = socialLoginHandler.getNaverLoginPageUrl();
        String googleLoginPageUrl = socialLoginHandler.getGoogleLoginPageUrl();

        model.addAttribute("kakaoLoginPageUrl", kakaoLoginPageUrl);
        model.addAttribute("naverLoginPageUrl", naverLoginPageUrl);
        model.addAttribute("googleLoginPageUrl", googleLoginPageUrl);

        return "/user/login";
    }

    //로그인 요청
    @PostMapping("/enter")
    public String login(UserVO userVO, HttpSession session, Model model) {
        // DB에서 user_id로 사용자 조회
        UserVO loginUser = loginService.findUserById(userVO.getUserId());

        if (loginUser == null) {
            // 사용자 ID가 없는 경우
            model.addAttribute("error", "존재하지 않는 사용자입니다.");
            return "/user/login"; // 다시 로그인 페이지로
        }

        if (!userVO.getPassword().equals(loginUser.getPassword())) {
            // 비밀번호가 틀린 경우
            model.addAttribute("error", "비밀번호가 틀렸습니다.");
            return "/user/login"; 
        }

        // 로그인 성공: 세션에 사용자 정보 저장
        session.setAttribute("loginUser", loginUser);
        log.info("로그인 성공: {}", loginUser);

        // 메인 페이지로 리다이렉트
        return "redirect:/";
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

        String googleEmail = socialLoginHandler.parseGoogleUserInfo(googleUserInfo);

        UserVO loginUser = loginService.findUserById(googleEmail);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            session.setAttribute("loginUser", loginUser);
            log.info("소셜 로그인 성공: {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", googleEmail); // 이메일을 모델에 담아 회원가입 페이지로 전달
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
        String parseNaverUserInfoResult =  socialLoginHandler.parseNaverUserInfo(naverUserInfo);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 유저 정보 >>>>>{}", parseNaverUserInfoResult);

        // 이메일로 회원이 존재하는지 확인
        // DB에서 user_id로 사용자 조회
        String[] dataParts = parseNaverUserInfoResult.split(",");
        String naverEmail = dataParts[0].split(":")[1].trim();
        String birthYear = dataParts[1].split(":")[1].trim();
        String birthDayRaw = dataParts[2].split(":")[1].trim();

        String[] birthDayParts = birthDayRaw.split("-");
        String month = birthDayParts[0];
        String day = birthDayParts[1];

        UserVO loginUser = loginService.findUserById(naverEmail);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            session.setAttribute("loginUser", loginUser);
            log.info("소셜 로그인 성공: {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", naverEmail);
            model.addAttribute("birthYear", birthYear);
            model.addAttribute("month", month);
            model.addAttribute("day", day);
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

        String kakaoEmail = socialLoginHandler.parseKakaoUserInfo(kakaoUserInfo);

        // 이메일로 회원이 존재하는지 확인
        // DB에서 user_id로 사용자 조회
        UserVO loginUser = loginService.findUserById(kakaoEmail);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            session.setAttribute("loginUser", loginUser);
            log.info("소셜 로그인 성공: {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", kakaoEmail); // 이메일을 모델에 담아 회원가입 페이지로 전달
            return "/user/join";
        }

        return "redirect:/";
    }




}
