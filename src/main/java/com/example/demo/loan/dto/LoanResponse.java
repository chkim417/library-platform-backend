package com.example.demo.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LoanResponse {
    private Long loanId;
    private LocalDate dueDate;
}
