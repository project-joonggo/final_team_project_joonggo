package com.project.joonggo.controller;

import com.project.joonggo.domain.LocationVO;
import com.project.joonggo.handler.TimeHandler;
import com.project.joonggo.service.LocationService;
import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.service.BoardService;
import com.project.joonggo.service.LoginService;
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
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WeatherController {

    /* 날씨 정보 */
    private final WeatherService weatherService;
    private final BoardService boardService;
    private final LoginService loginService;

    @Autowired
    private TimeHandler timeHandler;

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
        log.info("get: address");
        log.info("address: {}",address);
        log.info("parsedAddress: {}",parsedAddress);
        /* 만약 도로명 주소가 없다면(도로의 주소의 경우 도로명 주소가 없음 only 건물)*/
        if (address == null || address.isEmpty()) {
            address = parsedAddress;
        }
        log.info("address: {}",address);
        Map<String, String> lanLon = weatherService.returnLanLon(address);
        log.info("laLon: {}", lanLon);
        model.addAttribute("weather", weatherService.returnWeather(lanLon));

        // 각 상품에 대해 판매자 주소와 경과 시간을 조회하여 별도로 저장
        Map<Long, String> sellerAddresses = new HashMap<>();
        Map<Long, String> productTimes = new HashMap<>();  // 상품별 경과 시간 저장


        // 1. 최근 등록된 상품 18개
        List<BoardFileDTO> recentProducts = boardService.getRecentProducts();

        // 2. 실시간 인기 상품 18개 (조회 수 기준)
        List<BoardFileDTO> popularProducts = boardService.getPopularProducts();

        // 3. 추천 상품 18개 (좋아요 수 기준)
        List<BoardFileDTO> recommendedProducts = boardService.getRecommendedProducts();


        // 최근 등록된 상품에 대해 판매자 주소 조회
        for (BoardFileDTO product : recentProducts) {
            long sellerId = product.getBoardVO().getSellerId(); // 판매자 ID 가져오기
            if (!sellerAddresses.containsKey(sellerId)) {
                String sellerAddress = loginService.getSellerAddressByUserNum(sellerId);
                // sellerAddress가 null이 아닐 경우에만 처리
                if (sellerAddress != null) {
                    sellerAddress = sellerAddress.replace("(", "").replace(")", "");
                }
                sellerAddresses.put(sellerId, sellerAddress); // 판매자 주소를 맵에 저장
            }

            // regAt을 ISO 형식의 문자열로 변환
            String regAtString = product.getBoardVO().getRegAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // LocalDateTime -> String
            String timeAgo = TimeHandler.getTimeAgo(regAtString);  // 경과 시간 계산
            productTimes.put(product.getBoardVO().getBoardId(), timeAgo);  // 상품별 경과 시간 저장
        }

        // 인기 상품에 대해 판매자 주소 조회
        for (BoardFileDTO product : popularProducts) {
            long sellerId = product.getBoardVO().getSellerId();
            if (!sellerAddresses.containsKey(sellerId)) {
                String sellerAddress = loginService.getSellerAddressByUserNum(sellerId);
                sellerAddresses.put(sellerId, sellerAddress);
            }

            // regAt을 ISO 형식의 문자열로 변환
            String regAtString = product.getBoardVO().getRegAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // LocalDateTime -> String
            String timeAgo = TimeHandler.getTimeAgo(regAtString);  // 경과 시간 계산
            productTimes.put(product.getBoardVO().getBoardId(), timeAgo);  // 상품별 경과 시간 저장
        }

        // 추천 상품에 대해 판매자 주소 조회
        for (BoardFileDTO product : recommendedProducts) {
            long sellerId = product.getBoardVO().getSellerId();
            if (!sellerAddresses.containsKey(sellerId)) {
                String sellerAddress = loginService.getSellerAddressByUserNum(sellerId);
                sellerAddresses.put(sellerId, sellerAddress);
            }

            // regAt을 ISO 형식의 문자열로 변환
            String regAtString = product.getBoardVO().getRegAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // LocalDateTime -> String
            String timeAgo = TimeHandler.getTimeAgo(regAtString);  // 경과 시간 계산
            productTimes.put(product.getBoardVO().getBoardId(), timeAgo);  // 상품별 경과 시간 저장
        }

        log.info(">>>> {}, {} , {} >> " , recentProducts,popularProducts,recommendedProducts);
        log.info(">>> sell Addr >> {}" , sellerAddresses);
        log.info(">>> productTimes >>> {}" , productTimes);


        // 모델에 데이터 추가
        model.addAttribute("recentProducts", recentProducts);
        model.addAttribute("popularProducts", popularProducts);
        model.addAttribute("recommendedProducts", recommendedProducts);
        model.addAttribute("sellerAddresses", sellerAddresses);
        model.addAttribute("productTimes", productTimes);  // 상품별 경과 시간 추가


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