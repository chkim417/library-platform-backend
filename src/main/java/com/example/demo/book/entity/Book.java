package com.example.demo.book.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 도서번호

    private String title; // 도서명
    private String publisher; // 출판사
    private String author; // 지은이
    private String genre; // 장르
    private String tag; // 태그
    private String coverImage; // 표지이미지
    private Integer price; // 가격
    private LocalDate registrationDate; // 등록일

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private com.example.demo.loan.entity.Loan loan;
}
