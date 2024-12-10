package com.project.joonggo.controller;

import com.project.joonggo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

//    @Value("${KakaoMap_API_KEY}")
//    private String KakaoMap_API_KEY;

    @GetMapping
    public String home(){
        return "index";
    }

//    @GetMapping
//    public String home(Model model) {
//        model.addAttribute("KakaoMap_API_KEY", KakaoMap_API_KEY);
//        return "index";
//    }

    @PostMapping("/")
    public String showWeather(@RequestParam("address") String address, Model model){
        // 주소를 통해 위도와 경도를 얻고
        Map<String, String> lanLon = weatherService.returnLanLon(address);
        System.out.println("Latitude and Longitude: " + lanLon);  // 이를 통해 위도/경도 값이 정상적으로 들어오는지 확인
//        Map<String, String> weather = weatherService.returnWeather(lanLon);
//        System.out.println("Weather data: " + weather);  // 날씨 데이터가 정상적으로 반환되는지 확인


        model.addAttribute("weather", weatherService.returnWeather(lanLon));
        // 날씨 데이터를 모델에 담아 전달
//        model.addAttribute("weather", weather);
        return "index";
    }
}
