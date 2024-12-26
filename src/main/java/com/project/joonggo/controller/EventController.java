package com.project.joonggo.controller;

import com.project.joonggo.domain.GiftconVO;
import com.project.joonggo.domain.PagingVO;
import com.project.joonggo.handler.PagingHandler;
import com.project.joonggo.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;

// 바코드 import
/*import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;*/

@Controller
@RequiredArgsConstructor
@RequestMapping("/event/*")
@Slf4j
public class EventController {
    private final EventService eventService;

    @GetMapping("/list")
    public String showEventList() {

        return "/event/list";  // 이벤트 리스트 페이지로 이동
    }

    //당첨내역 조회페이지로 이동
    @GetMapping("/myGiftcon")
    public String myGiftcon(Model model, Principal principal, PagingVO pgvo){
        int totalCount = eventService.getMyGiftconTotal(pgvo, Long.parseLong(principal.getName()));
        PagingHandler ph = new PagingHandler(pgvo, totalCount);

        List<GiftconVO> list = eventService.getMyGiftconList(pgvo, Long.parseLong(principal.getName()));

        model.addAttribute("list", list);
        model.addAttribute("ph", ph);

        return "/event/myGiftcon";
    }

    @GetMapping("/roulette")
    public String showRouletteEvent() {

        return "/event/roulette";  // 룰렛 페이지로 이동
    }

    //룰렛 참여 여부 확인
    @GetMapping("/checkRoulette")
    public ResponseEntity<?> checkTodayParticipation(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        boolean alreadyParticipated = eventService.checkRouletteToday(userId);

        return ResponseEntity.ok(Map.of("alreadyParticipated", alreadyParticipated));
    }
    //룰렛 진행
    @PostMapping("/roulette")
    public ResponseEntity<?> handleRouletteResult(@RequestBody Map<String, String> request, Principal principal) {
        String result = request.get("result");
        Long userId = Long.parseLong(principal.getName()); // 로그인한 유저의 ID

        try {
            eventService.processRouletteResult(userId, result);
            return ResponseEntity.ok(Map.of("success", true, "message", "룰렛 결과 처리 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/attend")
    public String attend() {
        return "/event/attend";  // 출석체크 페이지로 이동
    }
    //출첵 참여 여부 확인
    @GetMapping("/checkAttend")
    public ResponseEntity<?> checkAttend(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        boolean alreadyParticipated = eventService.checkAttendToday(userId);

        return ResponseEntity.ok(Map.of("alreadyParticipated", alreadyParticipated));
    }
    //출첵 진행
    @PostMapping("/attend")
    public ResponseEntity<?> attend(@RequestBody Map<String, String> request, Principal principal) {
        String result = request.get("result");
        Long userId = Long.parseLong(principal.getName()); // 로그인한 유저의 ID

        try {
            boolean isGifted = eventService.processAttendResult(userId, result);
            return ResponseEntity.ok(Map.of("success", true, "message", "출첵 결과 처리 완료", "gift", isGifted));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    //출석count 조회
    @PostMapping("/attendCount")
    public ResponseEntity<?> attendCount(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        try {
            int attendCount = eventService.getAttendCount(userId); // 사용자 출석 횟수 조회
            return ResponseEntity.ok(Map.of("success", true, "attendCount", attendCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

}
