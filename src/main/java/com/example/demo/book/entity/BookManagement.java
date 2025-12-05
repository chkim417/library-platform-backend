package com.example.demo.book.entity;

import com.example.demo.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 도서관리번호

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Boolean isLoaned; // 대여유무
}
