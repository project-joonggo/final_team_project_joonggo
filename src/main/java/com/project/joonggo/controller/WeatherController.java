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
        Map<String, String> lanLon = weatherService.returnLanLon(address);

        model.addAttribute("weather", weatherService.returnWeather(lanLon));
        return "index";
    }
}
