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

    public ImageResponse generateImage(ImageRequest request) {

        if ((request.getPrompt() + SAFE_SUFFIX).length() > MAX_PROMPT_LENGTH) {
            ImageResponse error = new ImageResponse();
            error.setErrorMessage("프롬프트가 너무 깁니다.");
            return error;
        }

        String refinedPrompt = request.getPrompt() + SAFE_SUFFIX;

        // API 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey.trim());

        // 요청 Body 구성 (bookTitle 포함해야 함!)
        ImageRequest requestPayload =
                new ImageRequest(refinedPrompt, 1, "480x560", request.getBookTitle());

        HttpEntity<ImageRequest> entity = new HttpEntity<>(requestPayload, headers);

        ResponseEntity<ImageResponse> response =
                restTemplate.postForEntity(apiUrl, entity, ImageResponse.class);

        ImageResponse body = response.getBody();

        if (body != null && body.getData() != null && !body.getData().isEmpty()) {
            String imageUrl = body.getData().get(0).getUrl();

            try {
                // URL → BufferedImage
                BufferedImage image = ImageIO.read(new URL(imageUrl));

                if (image == null) {
                    body.setErrorMessage("이미지 다운로드 실패");
                    return body;
                }

                // PNG 변환
                ByteArrayOutputStream pngOut = new ByteArrayOutputStream();
                ImageIO.write(image, "png", pngOut);
                byte[] pngBytes = pngOut.toByteArray();

                // Base64 변환
                String base64 = Base64.getEncoder().encodeToString(pngBytes);

                // 저장 경로 생성
                String folderPath = "src/main/resources/img";
                Files.createDirectories(Paths.get(folderPath));

                Path savePath = Paths.get(folderPath + "/" + request.getBookTitle() + ".txt");

                // Base64 파일 저장
                Files.write(savePath, base64.getBytes());

                System.out.println("이미지 Base64 저장 완료: " + savePath);

            } catch (Exception e) {
                e.printStackTrace();
                body.setErrorMessage("이미지 처리 중 오류 발생");
            }

            return body;
        }

        ImageResponse empty = new ImageResponse();
        empty.setErrorMessage("이미지가 생성되지 않았습니다.");
        return empty;
    }
}
