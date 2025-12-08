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
    /** 입고할 수량 (정수). 프론트에서는 JSON으로 { "count": 5 } 형태로 보냄 */
    private Integer count;
}
