package com.project.joonggo.controller;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.PagingVO;
import com.project.joonggo.domain.UserVO;
import com.project.joonggo.handler.MailAuthHandler;
import com.project.joonggo.handler.PagingHandler;
import com.project.joonggo.handler.PhoneAuthHandler;
import com.project.joonggo.handler.SocialLoginHandler;
import com.project.joonggo.repository.UserMapper;
import com.project.joonggo.security.AuthUser;
import com.project.joonggo.service.BoardService;
import com.project.joonggo.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/user/*")
@RequiredArgsConstructor
@Controller
public class LoginController {
    private final LoginService loginService;
    private final BoardService boardService;
    private final PasswordEncoder passwordEncoder;
    private final PhoneAuthHandler phoneAuthHandler;
    private final SocialLoginHandler socialLoginHandler;
    private final UserMapper userMapper;
    private final MailAuthHandler mailAuthHandler;

    private static final int SIGN_FLAG_DEFAULT = 0;
    private static final int SIGN_FLAG_KAKAO = 1;
    private static final int SIGN_FLAG_NAVER = 2;
    private static final int SIGN_FLAG_GOOGLE = 3;

    private int mailAuthNumber;

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

    //아이디 찾기
    @GetMapping("/findId")
    public String findId(){
        return "/user/findId";
    }
    @PostMapping("/findId")
    public String findId(@RequestParam("name") String name,
                         @RequestParam("email") String email,
                         Model model) {
        List<UserVO> users = loginService.findByNameAndEmail(name, email);

        if (users.isEmpty()) {
            model.addAttribute("message", "입력한 정보와 일치하는 아이디를 찾을 수 없습니다.");
            model.addAttribute("status", "error");
        } else {
            model.addAttribute("userList", users);
            model.addAttribute("status", "success");
        }
        return "/user/findIdResult"; // 결과 페이지로 이동
    }

    //비밀번호 찾기
    @GetMapping("/findPassword")
    public String findPassword(){
        return "/user/findPassword";
    }
    //비밀번호 재설정
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("userId") String userId,
    @RequestParam("newPassword") String newPassword){
        String encodedPassword = passwordEncoder.encode(newPassword);
        loginService.updatePassword(userId, encodedPassword);
        return "redirect:/user/login";
    }

    //휴대폰 인증
    @ResponseBody
    @GetMapping("/phoneCheck")
    // 휴대폰 인증번호
    public String sendSMS(String phone){ // 휴대폰 문자보내기
        //난수 생성
        int ranNum = (int)((Math.random()*(9999-1000+1)) + 1000);
        log.info("===========================휴대폰 인증코드=============={}", ranNum);
        phoneAuthHandler.certifiedPhoneNumber(phone, ranNum);
        return Integer.toString(ranNum);
    }

    //이메일 인증
    // 인증 이메일 전송
    @PostMapping("/mailSend")
    public ResponseEntity<String> sendEmailAuth(@RequestParam String email) {
        try {
            // 인증번호 생성
            int authCode = mailAuthHandler.sendMail(email);
            return ResponseEntity.ok(String.valueOf(authCode)); // 생성된 인증번호를 문자열로 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error"); // 에러 메시지
        }
    }
    // 이메일 인증번호 일치여부 확인
    @GetMapping("/mailCheck")
    public ResponseEntity<Boolean> checkEmailAuth(@RequestParam String userNumber) {
        log.info("받은 인증번호: {}", userNumber); // 로그로 확인
        log.info("저장된 인증번호: {}", mailAuthNumber); // 로그로 확인
        boolean isMatch = userNumber.equals(String.valueOf(mailAuthNumber));
        return ResponseEntity.ok(isMatch);
    }

    //마이 페이지
    @GetMapping("/myInfo")
    public String myInfo(Model model, PagingVO pgvo, Principal principal){
        int totalCount = boardService.getMyTotal(pgvo, Long.parseLong(principal.getName()));
        PagingHandler ph = new PagingHandler(pgvo,totalCount);

        List<BoardFileDTO> list = boardService.getMyList(pgvo, Long.parseLong(principal.getName()));

        model.addAttribute("list", list);
        model.addAttribute("ph", ph);

        return "/user/myInfo";
    }
    @GetMapping("/modify")
    public String modify(){
        return "/user/modify";
    }
    //내정보 수정
    @PostMapping("/modify")
    public String modify(UserVO userVO){
        userVO.setPassword(passwordEncoder.encode(userVO.getPassword()));
        loginService.modify(userVO);
        return "redirect:/user/logout";
    }
    //회원 탈퇴
    @GetMapping("/delete")
    public String delete(Principal principal){
        long userNum = Long.parseLong(principal.getName()); // 문자열을 long으로 변환
        loginService.delete(userNum); // 수정된 서비스 호출
        return "redirect:/user/logout";
    }

    //////////////////////////
    /* 관리자 페이지 line */
    ////////////////////////
    // 관리자 페이지 이동
    @GetMapping("/admin")
    public String admin(){
        return "/user/admin";
    }
    // 신고관리 페이지
    @GetMapping("/reportList")
    public String list(Model model, PagingVO pgvo, @RequestParam(required = false) Integer reportCompId){
        int totalCount = boardService.getReportTotal(pgvo, reportCompId);
        PagingHandler ph = new PagingHandler(pgvo, totalCount);
        model.addAttribute("list", boardService.getReportList(pgvo, reportCompId));
        model.addAttribute("ph", ph);



        model.addAttribute("selectedCategory", reportCompId);
        // 신고 사유 목록을 가져와서 드롭다운에 추가할 수 있도록
        model.addAttribute("reasonList", boardService.getReasonList());


        log.info("reasonList >>>> {} ", boardService.getReasonList());

        return "/user/reportList";
    }

    // 신고상태 업데이트
    @PostMapping("/admin/updateReportStatus")
    public ResponseEntity<String> updateReportStatus(@RequestBody Map<String, Object> payload) {
        Long reportId = Long.valueOf(payload.get("reportId").toString());
        String status = payload.get("status").toString();
        boardService.updateReportStatus(reportId, status);
        return ResponseEntity.ok("Status updated successfully.");
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

    @GetMapping("/list")
    public String fraud(Model model, PagingVO pgvo,
                        @RequestParam(value = "keyword", required = false) String keyword){

        int totalCount = loginService.getTotal(pgvo);

        PagingHandler ph = new PagingHandler(pgvo,totalCount);

        List<UserVO> userList = loginService.getUserList(pgvo);

        log.info(">>> UserList >>> {}", userList);
        log.info(">>> ph >>> {}" , ph);
        log.info(">>> keyword >>> {}", keyword);

        model.addAttribute("userList", userList);
        model.addAttribute("ph",ph);
        model.addAttribute("keyword", keyword);

        return "/user/list";
    }


    // 사용자 추방
    @GetMapping("/user/ban")
    public String banUser(long userNum) {
        loginService.banUser(userNum);  // 서비스에서 추방 로직 호출
        return "redirect:/user/list";  // 추방 후 사용자 목록으로 리다이렉트
    }



}
