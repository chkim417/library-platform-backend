package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImageResponse {
    private long created;
    private List<ImageItem> data;

    @Data
    public static class ImageItem {
        private String url; // 만든 이미지 URL
    }
}