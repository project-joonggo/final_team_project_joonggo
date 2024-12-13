package com.project.joonggo.controller;

import com.project.joonggo.service.LocationService;
import com.project.joonggo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WeatherController {

    /* 날씨 정보 */
    private final WeatherService weatherService;

    /* DB 도로명 주소 정보 */
    private final LocationService locationService;

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
    public String showWeatherForm(@RequestParam(name = "address", required = false) String address, Principal principal , Model model) {
        /* 날씨 정보 */
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

        /* DB 도로명 주소 정보 */
        log.info("principal: {}", principal);
        if (principal != null) {
            int userNum = Integer.parseInt(principal.getName()); // 로그인된 사용자의 auto increment
            String streetAddress = locationService.getStreetAddress(userNum); // 주소 가져오기
            log.info("Street Address: {}", streetAddress);
            model.addAttribute("streetAddress", streetAddress);
        } else {
            log.info("로그인되지 않았습니다.");
            model.addAttribute("streetAddress", "로그인되지 않았습니다.");
        }
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