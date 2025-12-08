package com.example.demo.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnResponse {
    private String msg;
    private int penalty;
}
