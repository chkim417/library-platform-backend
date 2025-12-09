package com.example.demo.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    /** 대여 가능 재고 수량 */
    private Integer stockcount;
}
