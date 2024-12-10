package com.project.joonggo.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ImageHandler {
    public List<String> extractImageUrls(String content) {
        List<String> imageUrls = new ArrayList<>();

        log.info(content);

        if (content == null || content.isEmpty()) {
            log.warn("Content is null or empty. No image URLs to extract.");
            return imageUrls;  // 빈 리스트 반환
        }

        String regex = "<img[^>]+src=\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String imageUrl = matcher.group(1);  // 이미지 URL만 추출
            log.info(">>> matcher.group(1) >> {}", imageUrl);

            // 경로에 있는 역슬래시를 슬래시로 변경
            imageUrl = imageUrl.replace("\\", "/");

            log.info(">>> Updated imageUrl after replacing \\ with / >> {}", imageUrl);

            imageUrls.add(imageUrl);
            log.info(imageUrl);
        }

        log.info(" >> imageUrls>> {}" , imageUrls);

        return imageUrls;
    }
    public String extractUuidFromUrl(String fileUrl) {

        log.info(" >> in Url >> {} ", fileUrl);


        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;  // URL이 null이거나 비어있으면 UUID 추출 불가
        }

        // 예: http://localhost:8088/upload/2024/11/27/UUID_filename.jpg
        String regex = ".*(/([a-f0-9\\-]+)_.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileUrl);

        if (matcher.find()) {
            log.info(">>> uuid ?? >> {}" , matcher.group(2));
            return matcher.group(2);  // UUID 부분만 반환

        }
        return null;  // UUID가 없으면 null 반환
    }
    // URL에서 연도/날짜 부분만 추출하는 메서드 (upload 제외)
    public String extractSaveDir(String imageUrl) {
        // 예: "http://localhost:8089/upload/2024/11/27/33c8eb77-b55f-4d96-9e57-1c7529f2d5a8_강아지1.jpg"
        // -> "2024/11/27"만 추출

        // 정규 표현식으로 "upload/" 뒤의 연도/월/일을 추출
        Pattern pattern = Pattern.compile("upload/(\\d{4}/\\d{2}/\\d{2})/");
        Matcher matcher = pattern.matcher(imageUrl);

        if (matcher.find()) {
            // 매칭된 연도/월/일 부분 반환
            return matcher.group(1);
        }

        return ""; // 일치하는 부분이 없으면 빈 문자열 반환
    }

    // URL에서 파일 이름을 추출하는 메서드
    public String extractImageFileName(String imageUrl) {
        // 예: "http://localhost:8089/upload/2024/11/27/uuid_파일명.jpg"
        // -> "uuid_파일명.jpg"에서 파일 이름을 추출
        String[] parts = imageUrl.split("/");
        String fileNameWithUuid = parts[parts.length - 1];  // 마지막 부분이 파일 이름
        // 파일 이름에서 UUID 부분을 제거하고, 실제 파일 이름만 추출
        int underscoreIndex = fileNameWithUuid.indexOf('_');
        if (underscoreIndex != -1) {
            return fileNameWithUuid.substring(underscoreIndex + 1); // UUID 뒤의 파일 이름
        }
        return fileNameWithUuid;  // 예외 처리
    }


    public String removeImageUrlsFromContent(String content, List<String> imagesToDelete) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        // 정규 표현식으로 이미지 태그를 찾아서 삭제할 이미지 URL이 포함된 태그를 제거
        for (String imageUrl : imagesToDelete) {
            String regex = "<img[^>]+src=\"" + Pattern.quote(imageUrl) + "\"[^>]*>";
            content = content.replaceAll(regex, "");
        }

        return content;
    }
}
