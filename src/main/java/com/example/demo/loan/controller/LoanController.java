package com.example.demo.loan.controller;

import com.example.demo.loan.dto.LoanRequest;
import com.example.demo.loan.dto.LoanResponse;
import com.example.demo.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public LoanResponse loanBook(@RequestBody LoanRequest request) {
        return loanService.loanBook(request);
    }
}
