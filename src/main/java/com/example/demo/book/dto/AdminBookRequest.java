package com.example.demo.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminBookRequest {
    private String title;
    private String author;
    private String publisher;
    private String genre;
    private String tag;
    private String coverImageUrl;
    private Integer price;
    private LocalDate registrationDate;
    private String description; // 책소개
}
