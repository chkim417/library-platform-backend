package com.example.demo.loan.service;

import com.example.demo.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
}
