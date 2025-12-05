package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest {
    private String prompt; // 만들 이미지 설명
    private int n;         // 이미지 개수
    private String size;   // 크기
}