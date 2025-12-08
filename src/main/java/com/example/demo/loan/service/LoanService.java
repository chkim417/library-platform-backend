package com.example.demo.loan.service;

import com.example.demo.book.entity.BookManagement;
import com.example.demo.book.repository.BookManagementRepository;
import com.example.demo.loan.dto.LoanRequest;
import com.example.demo.loan.dto.LoanResponse;
import com.example.demo.loan.entity.Loan;
import com.example.demo.loan.repository.LoanRepository;
import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookManagementRepository bookManagementRepository;
    private final MemberRepository memberRepository;

    public LoanResponse loanBook(LoanRequest request) {

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));

        BookManagement bookManagement = bookManagementRepository
                .findFirstByBookIdAndIsLoanedFalse(request.getBookId())
                .orElseThrow(() -> new RuntimeException("대여 가능한 재고가 없습니다."));

        bookManagement.setIsLoaned(true); // 재고 상태 변경

        Loan loan = Loan.builder()
                .bookManagement(bookManagement)
                .member(member)
                .loanDate(LocalDate.now())
                .fee(0)
                .build();

        loanRepository.save(loan);

        LocalDate due = loan.getLoanDate().plusDays(7);

        return new LoanResponse(loan.getId(), due);
    }
}
