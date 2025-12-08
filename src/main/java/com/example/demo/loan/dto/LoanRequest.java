package com.example.demo.loan.dto;

import lombok.Data;

@Data
public class LoanRequest {
    private Long bookId;
    private String memberId; // JWT 미구현이므로 임시로 Body에서 받음
}
