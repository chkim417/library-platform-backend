package com.example.demo.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminBookResponse {
    private Long bookId;
    private String msg;
}
