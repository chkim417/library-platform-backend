package com.example.demo.book.controller;

import com.example.demo.book.dto.BookListResponse;
import com.example.demo.book.dto.BookRequest;
import com.example.demo.book.dto.BookResponse;
import com.example.demo.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService; // Service (팀원과의 계약) 주입

    // ==========================================
    // 1. 도서 목록 조회 (담당 영역: GET /api/books)
    // ==========================================
    @GetMapping
    public ResponseEntity<BookListResponse> getAllBooks(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "sort", defaultValue = "latest") String sort,
            @RequestParam(name = "keyword", required = false) String keyword) {

        // ★ [목록 조회/검색/페이지네이션 로직] Service에 위임
        BookListResponse response = bookService.getAllBooks(page, sort, keyword);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 2. 도서 검색 ( GET /api/books/search)
    // ==========================================
    @GetMapping("/search")
    public ResponseEntity<BookListResponse> searchBooks(
            @RequestParam(name = "keyword", required = false) String keyword) {

        // ★ [검색 로직] Service의 getAllBooks 메서드를 재사용하여 검색 위임
        BookListResponse response = bookService.getAllBooks(1, "latest", keyword);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 3. 도서 상세 조회 ( GET /api/books/{bookId})
    // ==========================================
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable(name = "bookId") Long bookId) {

        // ★ [상세 조회 로직] Service에 위임하여 상세 정보 및 재고 계산 요청
        BookResponse book = bookService.getBookById(bookId);
        return ResponseEntity.ok(book);
    }

}