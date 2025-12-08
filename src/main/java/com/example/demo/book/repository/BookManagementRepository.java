package com.example.demo.book.repository;

import com.example.demo.book.entity.BookManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BookManagementRepository extends JpaRepository<BookManagement, Long> {
    Optional<BookManagement> findFirstByBookIdAndIsLoanedFalse(Long bookId);

    /**
     * 특정 도서의 대출되지 않은(BookManagement.isLoaned == false) 레코드 목록 조회
     */
    List<BookManagement> findByBookIdAndIsLoanedFalse(Long bookId);

    /**
     * 특정 도서의 대출되지 않은 레코드 수(=현재 재고 수) 조회
     */
    int countByBookIdAndIsLoanedFalse(Long bookId);
}