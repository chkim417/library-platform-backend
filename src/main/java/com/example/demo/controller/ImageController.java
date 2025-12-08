package com.example.demo.controller;

import com.example.demo.dto.ImageRequest;
import com.example.demo.dto.ImageResponse;
import com.example.demo.service.OpenAiService;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.OpenAiService;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final OpenAiService openAiService;

    public ImageController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    // POST 요청으로 프롬프트를 받아 이미지 생성
    @PostMapping("/generate")
    public ImageResponse generateImage(@RequestBody ImageRequest request) {
        return openAiService.generateImage(request.getPrompt());
    }
}