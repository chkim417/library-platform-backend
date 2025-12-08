package com.example.demo.loan.service;

import com.example.demo.book.entity.BookManagement;
import com.example.demo.book.repository.BookManagementRepository;
import com.example.demo.loan.dto.ReturnResponse;
import com.example.demo.loan.entity.Loan;
import com.example.demo.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final LoanRepository loanRepository;
    private final BookManagementRepository bookManagementRepository;

    // 반납 처리
    public ReturnResponse returnBook(Long loanId) {
        // 대여 내역 조회
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("대여 정보가 없습니다."));

        // 연체료 계산
        LocalDate dueDate = loan.getLoanDate().plusDays(7);
        LocalDate today = LocalDate.now();

        int penalty = 0;
        if (today.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
            penalty = (int) daysLate * 1000; // 1일당 1000원 예시
        }

        // 재고 복구
        BookManagement book = loan.getBookManagement();
        book.setIsLoaned(false);
        bookManagementRepository.save(book);

        // Response
        return new ReturnResponse("반납완료", penalty);
    }
}
