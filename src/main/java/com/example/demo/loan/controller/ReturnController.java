package com.example.demo.loan.controller;

import com.example.demo.loan.dto.ReturnResponse;
import com.example.demo.loan.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    // PATCH /api/loans/{loanId}/return
    @PatchMapping("/{loanId}/return")
    public ReturnResponse returnBook(@PathVariable Long loanId) {
        return returnService.returnBook(loanId);
    }
}