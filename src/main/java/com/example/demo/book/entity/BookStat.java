package com.example.demo.book.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookStat {
    @Id
    private Long id; // 도서번호 (Shared PK)

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Book book;

    private Integer viewCount; // 노출수
    private Integer clickCount; // 클릭수
    private Integer loanCount; // 대여수
}
