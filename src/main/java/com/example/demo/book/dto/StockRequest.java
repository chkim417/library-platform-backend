package com.example.demo.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {
    /** 재고 증감 수량 (기본값 1) */
    private int stockcount = 1;
}
