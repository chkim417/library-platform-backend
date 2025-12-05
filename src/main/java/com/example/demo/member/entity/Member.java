package com.example.demo.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    private String id; // 회원번호 (String based on ERD VARCHAR(20))

    private String name;
    private String phone;
    private String address;
    private String password;
}
