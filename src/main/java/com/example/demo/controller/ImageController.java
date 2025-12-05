package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final OpenAiService openAiService;

    public ImageController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    // POST 요청으로 프롬프트를 받아 이미지 생성
    @PostMapping("/generate")
    public String generateImage(@RequestBody String prompt) {
        return openAiService.generateImage(prompt);
    }
}