package com.example.demo.cover.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 표지 생성 결과 DTO
 * - 최종적으로 저장된 이미지의 URL을 프론트로 내려준다.
 */
@Getter
@AllArgsConstructor
public class CoverResponse {

    private String imageUrl;  // 예: "/img/book/1/cover.png"
}
