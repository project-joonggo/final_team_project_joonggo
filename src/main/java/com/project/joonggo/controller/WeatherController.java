package com.project.joonggo.controller;

import com.project.joonggo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

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
            address="인천남동구구월동";
        }
        log.info("address: {}",address);
        Map<String, String> lanLon = weatherService.returnLanLon(address);
        model.addAttribute("weather", weatherService.returnWeather(lanLon));
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