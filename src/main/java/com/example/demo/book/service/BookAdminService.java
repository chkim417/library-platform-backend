package com.example.demo.book.service;

import com.example.demo.book.entity.Book;

public interface BookAdminService {

    /**
     * 도서를 생성하고, description이 있으면 BookDetail까지 함께 저장
     */
    Book createBook(Book book, String description);

    /**
     * 도서를 부분 수정하고, description이 전달되면 소개도 업데이트/삭제
     */
    Book updateBook(Long bookId, Book book, String description);

    /**
     * 도서와 연관 엔티티(BookDetail, 재고)를 함께 삭제
     */
    void deleteBook(Long bookId);
}
