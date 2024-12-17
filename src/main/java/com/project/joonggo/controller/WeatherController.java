package com.project.joonggo.controller;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.service.BoardService;
import com.project.joonggo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final BoardService boardService;

//    @Value("${KakaoMap_API_KEY}")
//    private String KakaoMap_API_KEY;

//    @GetMapping
//    public String home(){
//        return "index";
//    }

//    @GetMapping
//    public String home(Model model) {
//        model.addAttribute("KakaoMap_API_KEY", KakaoMap_API_KEY);
//        return "index";
//    }

    @GetMapping("/")
    public String showWeatherForm(@RequestParam(name = "address", required = false) String address, Model model) {
        // 날씨 정보를 기본적으로 model에 넣어서 전달
        // 예를 들어, 기본적인 날씨 정보나 기본값을 전달할 수 있습니다.
        Map<String, String> weather = new HashMap<>();
        log.info("get: address");
        log.info("address: {}",address);
        if(address == null){
            address="인천 남동구 구월동";
        }
        log.info("address: {}",address);
        Map<String, String> lanLon = weatherService.returnLanLon(address);
        model.addAttribute("weather", weatherService.returnWeather(lanLon));


        // 1. 최근 등록된 상품 18개
        List<BoardFileDTO> recentProducts = boardService.getRecentProducts();

        // 2. 실시간 인기 상품 18개 (조회 수 기준)
        List<BoardFileDTO> popularProducts = boardService.getPopularProducts();

        // 3. 추천 상품 18개 (좋아요 수 기준)
        List<BoardFileDTO> recommendedProducts = boardService.getRecommendedProducts();

        log.info(">>>> {}, {} , {} >> " , recentProducts,popularProducts,recommendedProducts);

        // 모델에 데이터 추가
        model.addAttribute("recentProducts", recentProducts);
        model.addAttribute("popularProducts", popularProducts);
        model.addAttribute("recommendedProducts", recommendedProducts);




        return "index";
    }

//    @PostMapping("/")
//    public String showWeather(@RequestParam("address") String address, Model model){
//        Map<String, String> lanLon = weatherService.returnLanLon(address);
//        log.info("post: 1");
//        log.info("address: {}",address);
//        model.addAttribute("weather", weatherService.returnWeather(lanLon));
//        return "index";
//    }

}