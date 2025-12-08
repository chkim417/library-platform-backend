package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImageResponse {

    private List<ImageItem> data;     // 이미지 URL 배열
    private String errorMessage;      // 에러 발생 시 메시지

    @Data
    public static class ImageItem {
        private String url;           // 생성된 이미지 URL
    }
}
