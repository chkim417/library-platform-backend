package com.example.demo.book.repository;

import com.example.demo.book.entity.BookManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookAdminRepository extends JpaRepository<BookManagement, Long> {
    Optional<BookManagement> findFirstByBookIdAndIsLoanedFalse(Long bookId);

    /**
     * 특정 도서의 대출되지 않은(BookManagement.isLoaned == false) 재고 목록 조회
     */
    List<BookManagement> findByBookIdAndIsLoanedFalse(Long bookId);

    /**
     * 특정 도서의 대출되지 않은 재고 수 조회
     */
    int countByBookIdAndIsLoanedFalse(Long bookId);

    /**
     * 특정 도서에 연관된 모든 BookManagement 재고 레코드 삭제
     * 도서 삭제 전에 호출하여 FK 제약을 방지
     */
    void deleteByBookId(Long bookId);
}
