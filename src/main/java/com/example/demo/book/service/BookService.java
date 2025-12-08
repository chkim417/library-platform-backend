package com.example.demo.book.service;

import com.example.demo.book.dto.BookListResponse;
import com.example.demo.book.dto.BookRequest;
import com.example.demo.book.dto.BookResponse;

public interface BookService {

    // CRUD 기능의 계약 정의
    BookListResponse getAllBooks(Integer page, String sort, String keyword);

    BookResponse getBookById(Long bookNo);

    Book createBook(Book book);
}.