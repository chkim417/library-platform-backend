package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImageResponse {

    private List<ImageItem> data;   // 이미지 URL 리스트
    private String errorMessage;    // 오류 발생 시 메시지
    private String localUrl;

    @Data
    public static class ImageItem {
        private String url;     // 생성된 이미지 URL
    }
}
