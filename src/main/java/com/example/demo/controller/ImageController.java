package com.example.demo.controller;

import com.example.demo.dto.ImageRequest;
import com.example.demo.dto.ImageResponse;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.OpenAiService;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final OpenAiService openAiService;

    public ImageController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/generate")
    public String generateImage(@RequestBody ImageRequest request) {
        return openAiService.generateImage(request);   // DTO 전체 전달
    }
}
