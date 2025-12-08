package com.example.demo.book.controller;

import com.example.demo.book.dto.AdminBookRequest;
import com.example.demo.book.dto.AdminBookResponse;
import com.example.demo.book.dto.StockRequest;
import com.example.demo.book.dto.StockResponse;
import com.example.demo.book.entity.Book;
import com.example.demo.book.service.BookInventoryService;
import com.example.demo.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {
    private final BookInventoryService inventoryService;
    private final BookService bookService;
    /**
     * 관리자용 재고 입고 엔드포인트
     * POST /admin/books/{bookId}/stock
     * 요청 바디: { "count": <입고수량> }
     * 응답: 현재 재고 수를 담은 StockResponse
     */
    @PostMapping("/{bookId}/stock")
    public StockResponse restock(@PathVariable Long bookId, @RequestBody StockRequest request) {
        // 요청에 count가 없으면 0으로 처리
        int current = inventoryService.restock(bookId, request.getCount() == null ? 0 : request.getCount());
        return new StockResponse(current);
    }

    // 관리자 도서 등록: POST /admin/books
    @PostMapping
    public AdminBookResponse createBook(@RequestBody AdminBookRequest req) {
        Book book = Book.builder()
                .title(req.getTitle())
                .author(req.getAuthor())
                .publisher(req.getPublisher())
                .genre(req.getGenre())
                .tag(req.getTag())
                .coverImage(req.getCoverImageUrl())
                .price(req.getPrice())
                .registrationDate(req.getRegistrationDate() == null ? LocalDate.now() : req.getRegistrationDate())
                .build();

        Book saved = bookService.createBook(book);
        return new AdminBookResponse(saved.getId(), "등록완료");
    }

    /**
     * 관리자 도서 삭제
     * DELETE /admin/books/{bookId}
     */
    @DeleteMapping("/{bookId}")
    public AdminBookResponse deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return new AdminBookResponse(bookId, "삭제완료");
    }
}