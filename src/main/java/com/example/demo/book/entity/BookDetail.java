package com.example.demo.book.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetail {
    @Id
    private Long id; // 도서번호 (Shared PK)

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Book book;

    @Lob
    private String description; // 책소개
}
