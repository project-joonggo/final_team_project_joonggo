package com.project.joonggo.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.joonggo.domain.*;
import com.project.joonggo.handler.*;
import com.project.joonggo.service.BoardService;
import com.project.joonggo.service.LoginService;
import com.project.joonggo.service.NotificationService;
import com.project.joonggo.service.WishService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/*")
@Slf4j
public class BoardController {
    @Autowired
    private ImageHandler imageHandler;

    private final BoardService boardService;
    private final FileDeleteHandler fileDeleteHandler;
    private final WishService wishService;
    private final LoginService loginService;
    private final NotificationService notificationService;


    @Autowired
    private FileHandler fileHandler;


    @GetMapping("/register")
    public void register(){}


    @PostMapping("/register")
    @ResponseBody
    public String register(@ModelAttribute BoardVO boardVO, Principal principal){
        log.info("boardVO >>> {} ", boardVO);

        Long userNum = Long.valueOf(principal.getName());

        log.info(">>> userNum >> {}", userNum);

        boardVO.setSellerId(userNum); // sellerId가 userNum

        long isOk = boardService.register(boardVO);

        log.info("isOk >>>> {}", isOk);

        return (isOk > 0) ? "1" : "0";
    }

    @PostMapping("/multiFileUpload")
    public ResponseEntity<List<FileVO>> multiFileUpload(@RequestParam("files") MultipartFile[] files){

        List<FileVO> flist = null;

        if( files != null && files[0].getSize() > 0){
            flist = fileHandler.uploadFiles(files);
            log.info(">>>> flist >> {} " , flist.toString());
            int isOk = boardService.fileUpload(flist);
        }

        return ResponseEntity.ok(flist);
    }

    @GetMapping("/list")
    public String list(Model model, PagingVO pgvo, @RequestParam(value = "category",required = false) String category,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "minPrice", required = false) Integer minPrice,
                       @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
                       @RequestParam(value = "includeSoldOut", required = false, defaultValue = "false") boolean includeSoldOut,
                       @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort){



        int totalCount = boardService.getTotal(pgvo);

        // getList에서만 15개씩 가져오기 위해 PagingVO를 새로 생성하여 15로 설정
        PagingVO customPagingVO = new PagingVO(pgvo.getPageNo(), 15);  // 여기서 15로 개수를 설정
        customPagingVO.setCategory(pgvo.getCategory());  // category 필터 값 설정
        customPagingVO.setKeyword(pgvo.getKeyword());    // keyword 필터 값 설정
        customPagingVO.setMinPrice(pgvo.getMinPrice());  // minPrice 필터 값 설정
        customPagingVO.setMaxPrice(pgvo.getMaxPrice());  // maxPrice 필터 값 설정
        customPagingVO.setIncludeSoldOut(pgvo.isIncludeSoldOut()); // 품절 여부 설정
        customPagingVO.setSort(pgvo.getSort());  // 정렬 기준 설정

        PagingHandler ph = new PagingHandler(customPagingVO,totalCount);

        List<BoardFileDTO> list = boardService.getList(customPagingVO);

        log.info(">>> list >>> {}", list);
        log.info(">>> ph >>> {}" , ph);
        log.info(">>> category >> {}" , category);
        log.info(">>> keyword >>> {}", keyword);
        log.info(">>> minPrice >>> {}", minPrice);
        log.info(">>> maxPrice >>> {}", maxPrice);
        log.info(">>> includeSoldOut >>> {}", includeSoldOut);
        log.info(">>> sort >>> {}", sort);

        // 평균, 최소, 최대 가격 계산
        double averagePrice = 0.0;
        int minPriceValue = Integer.MAX_VALUE;
        int maxPriceValue = Integer.MIN_VALUE;
        int priceCount = 0;

        for (BoardFileDTO dto : list) {
            int price = dto.getBoardVO().getTradePrice();  // 가격 정보를 가져옴
            if (price > 0) {
                averagePrice += price;
                minPriceValue = Math.min(minPriceValue, price);
                maxPriceValue = Math.max(maxPriceValue, price);
                priceCount++;
            }
        }

        if (priceCount > 0) {
            averagePrice /= priceCount;  // 평균 가격 계산
        } else {
            averagePrice = 0.0;  // 가격이 없는 경우
            minPriceValue = 0;   // 최소 가격은 0
            maxPriceValue = 0;   // 최대 가격은 0
        }

        // 가격을 3자리마다 콤마로 구분하고, 평균 가격 소수점 버리기
        NumberFormat numberFormat = NumberFormat.getInstance();
        DecimalFormat decimalFormat = new DecimalFormat("#,###"); // 소수점 버리고 3자리마다 콤마

        // 포맷된 값으로 변환
        String formattedAveragePrice = decimalFormat.format(averagePrice);  // 평균 가격
        String formattedMinPrice = numberFormat.format(minPriceValue);      // 최소 가격
        String formattedMaxPrice = numberFormat.format(maxPriceValue);      // 최대 가격

        log.info(">>> 평균 가격 >>> {}", formattedAveragePrice);
        log.info(">>> 최소 가격 >>> {}", formattedMinPrice);
        log.info(">>> 최대 가격 >>> {}", formattedMaxPrice);

        // 최근 등록된 상품에 대해 판매자 주소 및 경과 시간 조회
        Map<Long, String> sellerAddresses = new HashMap<>();
        Map<Long, String> productTimes = new HashMap<>();

        for (BoardFileDTO product : list) {
            long sellerId = product.getBoardVO().getSellerId(); // 판매자 ID 가져오기
            if (!sellerAddresses.containsKey(sellerId)) {
                String sellerAddress = loginService.getSellerAddressByUserNum(sellerId);
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

        // 모델에 sellerAddresses와 productTimes 추가
        model.addAttribute("sellerAddresses", sellerAddresses);
        model.addAttribute("productTimes", productTimes);

        // 모델에 평균, 최소, 최대 가격 추가
        model.addAttribute("formattedAveragePrice", formattedAveragePrice);
        model.addAttribute("formattedMinPrice", formattedMinPrice);
        model.addAttribute("formattedMaxPrice", formattedMaxPrice);

        model.addAttribute("list",list);
        model.addAttribute("ph",ph);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("includeSoldOut", includeSoldOut);  // 추가된 필드 설정
        model.addAttribute("sort", sort); // sort 값도 모델에 추가

        return "/board/list";
    }

    @GetMapping({"/detail","/modify"})
    public void detail(Model model, @RequestParam("boardId") Long boardId, HttpServletRequest request, Principal principal){

        // 현재 요청 URL 확인
        String requestURI = request.getRequestURI();

        // 'detail' 페이지에서만 조회수를 증가시킴
        if (requestURI.contains("/detail")) {
            boardService.incrementReadCount(boardId);  // 조회수 증가
        }

        // 로그인된 사용자 정보 가져오기

        Long userNum = principal != null ?  Long.valueOf(principal.getName()) : null;

        log.info(">>> userNum >>> {}", userNum);

        // 로그인한 사용자만 찜 상태 확인
        boolean isAlreadyWished = false;
        if (userNum != null) {
            // 사용자가 찜한 상태인지 확인
            isAlreadyWished = wishService.isAlreadyWished(userNum, boardId);
        }
        log.info(">>>> isAlreadyWished >> {}" , isAlreadyWished);

        BoardFileDTO boardFileDTO = boardService.getDetail(boardId);

        // 신고 사유 목록 전달
        List<ReasonVO> reasonList = boardService.getReasonList();
        // 최근 등록된 상품에 대해 판매자 주소 및 경과 시간 조회
        Map<Long, String> sellerAddresses = new HashMap<>();
        Map<Long, String> productTimes = new HashMap<>();

        long sellerId = boardFileDTO.getBoardVO().getSellerId(); // 판매자 ID 가져오기

        String sellerAddress = loginService.getSellerAddressByUserNum(sellerId);

        sellerAddress = sellerAddress.replace("(", "").replace(")", "");

        // regAt을 ISO 형식의 문자열로 변환
        String regAtString = boardFileDTO.getBoardVO().getRegAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // LocalDateTime -> String
        String timeAgo = TimeHandler.getTimeAgo(regAtString);  // 경과 시간 계산

        log.info("sellerAddr , timeAgo >>>>> {} , {}  " , sellerAddress, timeAgo);

        // 모델에 sellerAddresses와 productTimes 추가
        model.addAttribute("sellerAddress", sellerAddress);
        model.addAttribute("timeAgo", timeAgo);

        model.addAttribute("boardFileDTO", boardFileDTO);
        model.addAttribute("isAlreadyWished", isAlreadyWished);
        model.addAttribute("reasonList", reasonList);
    }


    @PostMapping("/update")
    public String update(@ModelAttribute BoardVO boardVO){

        if (boardVO.getTradeFlag() == 1) {
            log.info(">>> 거래 완료된 게시글은 수정할 수 없습니다.");
            return "redirect:/board/detail?boardId=" + boardVO.getBoardId(); // 수정 불가능한 페이지로 리다이렉트, 사용자한테는 js에서 alert로 경고문
        }

        log.info(">>>> boardVO >> {}", boardVO);
        // 수정된 컨텐츠 가져와서 이미지 url만 빼기
        List<String> existingImages = imageHandler.extractImageUrls(boardVO.getBoardContent());

        // 기존 이미지 목록과 비교하여 삭제된 이미지들을 추출
        List<String> updatedImages = imageHandler.extractImageUrls(boardService.getUpdateContent(boardVO.getBoardId())); // 서버에서 파일에 저장돼있는 정보를 가져옴
        log.info(">>> updateImages >> {}" , updatedImages);
        List<String> imagesToDelete = new ArrayList<>(updatedImages);
        log.info(">>> imagesToDelete1 >> {} " , imagesToDelete);

        imagesToDelete.removeAll(existingImages);

        log.info(">>> imagesToDelete2 >> {} " , imagesToDelete);

        for (String imageUrl : imagesToDelete) {
            log.info(">>> imageUrl >> {}" , imageUrl);
            String uuid = imageHandler.extractUuidFromUrl(imageUrl);  // 이미지 URL에서 UUID 추출
            log.info(">>> uuid >>> {}" , uuid);
            if (uuid != null) {
                String imageFileName = imageHandler.extractImageFileName(imageUrl);
                log.info(">>> imageFileName ?? > {}" , imageFileName);
                String saveDir = imageHandler.extractSaveDir(imageUrl);
                log.info(">>> saveDir ?? > {}", saveDir);
                // 실제파일 삭제
                fileDeleteHandler.deleteFile(saveDir,uuid,imageFileName);
                // DB에서 파일 정보 삭제 ( is_del = 'Y')
                boardService.deleteFileFromDB(uuid);
            }
        }

        String updatedContent = imageHandler.removeImageUrlsFromContent(boardVO.getBoardContent(), imagesToDelete);

        log.info("updateContent >>>> {}" , updatedContent);

        boardVO.setBoardContent(updatedContent);

        log.info("boardDTO.getUpdateContent >>>> {}" , boardVO.getBoardContent());

        boardService.updateBoardContent(boardVO);

        return  "redirect:/board/detail?boardId=" + boardVO.getBoardId();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("boardId") Long boardId){

        boardService.boardIsDelUpdate(boardId);

        return "redirect:/board/list";
    }

    @GetMapping("/price")
    public String price(Model model, @RequestParam(required = false) String keyword) throws JsonProcessingException {

        // 만약 keyword가 있다면, 해당 keyword로 상품 조회를 실행
        List<BoardFileDTO> productList = boardService.searchPrice(keyword);

        int averagePrice = 0;
        int maxPrice = 0;
        int minPrice = 0;

        // 2. 시세 계산 (평균 가격, 최대 가격, 최소 가격)
        if (productList != null && !productList.isEmpty()) {
            int totalPrice = 0;
            maxPrice = Integer.MIN_VALUE;
            minPrice = Integer.MAX_VALUE;

            // 가격을 하나씩 확인하면서 계산
            for (BoardFileDTO  product : productList) {
                int price = product.getBoardVO().getTradePrice();
                totalPrice += price;

                if (price > maxPrice) {
                    maxPrice = price;
                }
                if (price < minPrice) {
                    minPrice = price;
                }
            }

            // 평균 계산
            averagePrice = totalPrice / productList.size();
        }

        // 3. 최근 등록된 상품 20개 조회
        List<BoardFileDTO> recentProducts = new ArrayList<>();
        if (productList != null && !productList.isEmpty()) {
            // 상품들을 최근 등록일 순으로 정렬
            productList.sort((p1, p2) -> p2.getBoardVO().getRegAt().compareTo(p1.getBoardVO().getRegAt()));

            // 최근 20개만 추출
            for (int i = 0; i < Math.min(20, productList.size()); i++) {
                BoardFileDTO boardFileDTO = productList.get(i);  // BoardFileDTO 객체를 가져옴
                List<FileVO> files = boardFileDTO.getFileVOList();     // 해당 BoardFileDTO에서 파일 목록을 가져옴
                String fileUrl = null;

                // 파일이 존재하면 첫 번째 파일 URL을 사용
                if (files != null && !files.isEmpty()) {
                    fileUrl = files.get(0).getFileUrl(); // 첫 번째 파일 URL을 추출 (필요시 다르게 처리 가능)
                }

                // 이미지가 있는 상품만 recentProducts 리스트에 추가
                if (fileUrl != null && !fileUrl.isEmpty()) {
                    recentProducts.add(boardFileDTO);
                }
            }
        }

        // 4. 최근 15일의 평균 가격 조회
        List<Map<String, Object>> avgPriceData = boardService.getAvgPriceForLast15Days(keyword);

        String avgPriceDataJson = new ObjectMapper().writeValueAsString(avgPriceData);

        log.info(">>> avgPriceData >>> {}", avgPriceData);
        log.info(">>>avgPriceDataJson >>> {}", avgPriceDataJson);

        model.addAttribute("avgPriceDataJson", avgPriceDataJson);

        log.info(">>> productList >>> {}", productList);
        log.info(">>> keyword >>>> {}", keyword);
        log.info(">>> averagePrice >>> {}", averagePrice);
        log.info(">>> maxPrice >>> {}", maxPrice);
        log.info(">>> minPrice >>> {}", minPrice);
        log.info(">>> recentProducts >>> {}", recentProducts);

        // 가격을 3자리마다 콤마(,)로 구분
        NumberFormat numberFormat = NumberFormat.getInstance();
        String formattedAveragePrice = numberFormat.format(averagePrice);
        String formattedMaxPrice = numberFormat.format(maxPrice);
        String formattedMinPrice = numberFormat.format(minPrice);

        // 모델에 상품 목록과 검색어 추가
        model.addAttribute("productList", productList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("averagePrice", formattedAveragePrice);  // 평균 가격 (포맷팅된 값)
        model.addAttribute("maxPrice", formattedMaxPrice);          // 최대 가격 (포맷팅된 값)
        model.addAttribute("minPrice", formattedMinPrice);          // 최소 가격 (포맷팅된 값)
        model.addAttribute("recentProducts", recentProducts); // 최근 등록된 상품

        return "/board/price";  // 가격 조회 화면으로 리턴
    }

    @PostMapping("/report")
    public String report(@RequestParam("reasonId") Long reasonId,
                         @RequestParam("sellerId") Long sellerId,
                         @RequestParam("boardId") Long boardId,
                         @RequestParam("reporterId") Long reporterId,
                         RedirectAttributes redirectAttributes) {
        ReportVO reportVO = new ReportVO();
        reportVO.setReportCompId(reasonId);  // 신고 사유
        reportVO.setUserNum(sellerId);  // 신고 당할 사람
        reportVO.setBoardId(boardId);
        reportVO.setReporterNum(reporterId);
        boardService.saveReport(reportVO);

        Long adminId = loginService.getAdminId();

        // 알림 메시지 생성
        String notificationMessage = "<span>신고</span><br>새로운 신고가 접수되었습니다.<br> 신고된 게시글 번호: " + boardId;

        // 알림을 관리자에게 보내기
        notificationService.saveNotification(adminId, notificationMessage, boardId, "REPORT");

        redirectAttributes.addFlashAttribute("alertMessage", "신고가 접수되었습니다. 관리자 확인 후 조치될 예정입니다.");

        return "redirect:/board/detail?boardId=" + boardId;  // 신고 후, 게시글 상세 페이지로 리다이렉트
    }

    @GetMapping("/fraud")
    public String fraud(Model model,
                        @RequestParam(value = "keyword", required = false) String keyword){

        List<Map<String, Object>> fraudUserList = null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            fraudUserList = loginService.searchFraudUsers(keyword);
        }

        log.info(">>>>> fraudUserList > >> {}" , fraudUserList);

        model.addAttribute("fraudUserList", fraudUserList);
        return "/board/fraud";
    }

}
