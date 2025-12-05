package com.example.demo.loan.entity;

import com.example.demo.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 대여번호

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private Integer fee; // 대여료
    private LocalDate loanDate; // 대여기간
}
