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

@Service
public class OpenAiService {

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public OpenAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // HttpComponentsClientHttpRequestFactory 적용
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public ImageResponse generateImage(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey.trim());

        ImageRequest requestPayload = new ImageRequest(prompt, 1, "1024x1024");
        HttpEntity<ImageRequest> entity = new HttpEntity<>(requestPayload, headers);

        ResponseEntity<ImageResponse> response = restTemplate.postForEntity(
                apiUrl,
                entity,
                ImageResponse.class
        );

        ImageResponse body = response.getBody();

        if (body != null && body.getData() != null && !body.getData().isEmpty()) {
            return body;  // 정상 응답
        }

        // 실패 응답
        ImageResponse emptyResponse = new ImageResponse();
        emptyResponse.setErrorMessage("이미지가 생성되지 않았습니다.");
        return emptyResponse;
    }

}

