package com.project.joonggo.controller;

import com.project.joonggo.domain.LocationVO;
import com.project.joonggo.service.LocationService;
import com.project.joonggo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
//    private final LocationService locationService;

    @Autowired
    private LocationService locationService;

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

    // @ModelAttribute를 사용하여 모든 요청에서 공통적으로 사용할 데이터를 추가
/*    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal) {
        LocationVO location = new LocationVO();

        *//* DB 도로명 주소 정보 *//*
        String parsedAddress = "";
        log.info("principal: {}", principal);
        if (principal != null) {
            int userNum = Integer.parseInt(principal.getName()); // 로그인된 사용자의 auto increment
            String streetAddress = locationService.getStreetAddress(userNum); // 주소 가져오기
//            model.addAttribute("streetAddress", streetAddress);
            location.setStreetAddress(streetAddress);

            // 도로명 주소 동까지 자르기 (ex: 인천 남동구 구월동)
            String[] addressParts = streetAddress.split(" ");
            String part1 = addressParts[0];
            String part2 = addressParts[1];
            String part3 = addressParts[2];
            parsedAddress = String.join(" ", part1, part2, part3);
//            model.addAttribute("parsedAddress", parsedAddress);
            location.setParsedAddress(parsedAddress); // parsedAddress 설정

            model.addAttribute("location", location); // 모델에 LocationVO 객체 전달
            model.addAttribute("parsedAddress", parsedAddress);
            log.info("location: {}", location);
        } else {
            model.addAttribute("location", new LocationVO()); // 모델에 LocationVO 객체 전달
            model.addAttribute("parsedAddress", "");
            log.info("location: {}", location);
        }
    }*/

    @GetMapping("/")
    public String showWeatherForm(@RequestParam(name = "address", required = false) String address, Model model, Principal principal) {
//        String parsedAddress = (String) model.getAttribute("parsedAddress");

        LocationVO location = new LocationVO();

        /* DB 도로명 주소 정보 */
        String parsedAddress = "";
        log.info("principal: {}", principal);
        if (principal != null) {
            int userNum = Integer.parseInt(principal.getName()); // 로그인된 사용자의 auto increment
            String streetAddress = locationService.getStreetAddress(userNum); // 주소 가져오기
//            model.addAttribute("streetAddress", streetAddress);
            location.setStreetAddress(streetAddress);

            // 도로명 주소 동까지 자르기 (ex: 인천 남동구 구월동)
            String[] addressParts = streetAddress.split(" ");
            String part1 = addressParts[0];
            String part2 = addressParts[1];
            String part3 = addressParts[2];
            parsedAddress = String.join(" ", part1, part2, part3);
//            model.addAttribute("parsedAddress", parsedAddress);
            location.setParsedAddress(parsedAddress); // parsedAddress 설정

            model.addAttribute("location", location); // 모델에 LocationVO 객체 전달
            model.addAttribute("parsedAddress", parsedAddress);
            log.info("location: {}", location);
        } else {
            model.addAttribute("location", new LocationVO()); // 모델에 LocationVO 객체 전달
            model.addAttribute("parsedAddress", "");
            log.info("location: {}", location);
        }

        /* 날씨 정보 */
        // 날씨 정보를 기본적으로 model에 넣어서 전달
        // 예를 들어, 기본적인 날씨 정보나 기본값을 전달할 수 있습니다.
        Map<String, String> weather = new HashMap<>();
        log.info("get: address");
        log.info("address: {}",address);
        log.info("parsedAddress: {}",parsedAddress);
        /* 만약 도로명 주소가 없다면(도로의 주소의 경우 도로명 주소가 없음 only 건물)*/
        if (address == null || address.isEmpty()) {
            address = parsedAddress;
        }
//        if (parsedAddress != null && !parsedAddress.isEmpty()) {
//            address = parsedAddress;  // parsedAddress가 있을 경우 사용
//        }
        log.info("final address: {}", address);
        Map<String, String> lanLon = weatherService.returnLanLon(address);
        log.info("laLon: {}", lanLon);
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