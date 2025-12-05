package com.example.demo.cover.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 표지 생성 요청 DTO
 * - 어떤 책(bookId)에 대한 표지인지
 * - 이미지 설명(prompt)
 * - (옵션) 이미 생성된 base64 이미지를 그대로 받아서 저장만 할 수도 있음
 */
@Getter
@Setter
@NoArgsConstructor
public class CoverRequest {

    private Long bookId;          // 어느 책의 표지인지 (Book PK)
    private String title;         // 책 제목 (AI 프롬프트용)
    private String description;   // 책 설명/키워드 (AI 프롬프트용)

    // GPT에서 이미지를 생성해줬다면 여기로 base64 그대로 받아도 됨
    private String base64Image;   // "data:image/png;base64,..." 또는 순수 base64
}
