package com.example.demo.service;

import com.example.demo.dto.ImageRequest;
import com.example.demo.dto.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private static final String SAFE_SUFFIX =
            " Book cover generation. Keep it clean, professional, and appropriate for all ages. No sexual, violent, or offensive elements.";

    private static final int MAX_PROMPT_LENGTH = 1000;

    public OpenAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public String generateImage(ImageRequest request) {

        if ((request.getPrompt() + SAFE_SUFFIX).length() > MAX_PROMPT_LENGTH) {
            return "프롬프트가 너무 깁니다.";
        }
        String path = "";

        String refinedPrompt = request.getPrompt();

        // API 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey.trim());
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("prompt", refinedPrompt);
        requestPayload.put("n", 1);
        requestPayload.put("size", "512x512");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<ImageResponse> response = restTemplate.postForEntity(apiUrl, entity, ImageResponse.class);

        ImageResponse body = response.getBody();

        if (body != null && body.getData() != null && !body.getData().isEmpty()) {
            String imageUrl = body.getData().get(0).getUrl();

            try {
                // URL → BufferedImage
                BufferedImage image = ImageIO.read(new URL(imageUrl));

                if (image == null) {
//                    body.setErrorMessage("이미지 다운로드 실패");
                    return "이미지 다운로드 실패";
                }

                // PNG 변환
                ByteArrayOutputStream pngOut = new ByteArrayOutputStream();
                ImageIO.write(image, "png", pngOut);
                byte[] pngBytes = pngOut.toByteArray();

                // Base64 변환
//                String base64 = Base64.getEncoder().encodeToString(pngBytes);

                // 저장 경로 생성
                String folderPath = "src/main/resources/img";
                Files.createDirectories(Paths.get(folderPath));
                path = folderPath + "/" + request.getTitle();

                Path savePath = Paths.get(path + ".png");

                // Base64 파일 저장
//                Files.write(savePath, base64.getBytes());
                Files.write(savePath, pngBytes);

//                System.out.println("이미지 Base64 저장 완료: " + savePath);

            } catch (Exception e) {
                e.printStackTrace();
                body.setErrorMessage("이미지 처리 중 오류 발생");
            }

            return path;
        }

        return "이미지가 생성되지 않았습니다.";
    }
}
