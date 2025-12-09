package com.example.demo.book.controller;

import com.example.demo.book.dto.AdminBookRequest;
import com.example.demo.book.dto.AdminBookResponse;
import com.example.demo.book.dto.StockRequest;
import com.example.demo.book.dto.StockResponse;
import com.example.demo.book.entity.Book;
import com.example.demo.book.service.BookAdminService;
import com.example.demo.book.service.BookInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookInventoryService inventoryService;
    private final BookAdminService bookAdminService;

    /**
     * 관리자 대여가능여부 확인 및 증감
     * POST /admin/books/{bookId}/stock
     * 요청 바디: { "count": <증감 수량, 기본 1 / 0이면 대여 불가로 처리> }
     * 응답: { "stockcount": <대출 가능 재고 수량> }
     */
    @PostMapping("/{bookId}/stock")
    public StockResponse checkAvailability(@PathVariable("bookId") Long bookId,
                                           @RequestBody StockRequest request) {
        int count = request.getStockcount(); // 기본 1
        int current = inventoryService.restock(bookId, count);
        return new StockResponse(current);
    }

    // 관리자 도서 등록: POST /admin/books
    @PostMapping
    public AdminBookResponse createBook(@RequestBody AdminBookRequest req) {
        Book book = buildBookFromRequest(req, true);
        Book saved = bookAdminService.createBook(book, req.getDescription());
        return new AdminBookResponse(saved.getId(), "등록완료");
    }

    // 관리자 도서 수정: PATCH /admin/books/{bookId}
    @PatchMapping("/{bookId}")
    public AdminBookResponse updateBook(@PathVariable("bookId") Long bookId,
                                        @RequestBody AdminBookRequest req) {
        Book book = buildBookFromRequest(req, false);
        Book updated = bookAdminService.updateBook(bookId, book, req.getDescription());
        return new AdminBookResponse(updated.getId(), "수정완료");
    }

    /**
     * 관리자 도서 삭제
     * DELETE /admin/books/{bookId}
     */
    @DeleteMapping("/{bookId}")
    public AdminBookResponse deleteBook(@PathVariable("bookId") Long bookId) {
        bookAdminService.deleteBook(bookId);
        return new AdminBookResponse(bookId, "삭제완료");
    }

    /**
     * AdminBookRequest를 Book 엔티티로 변환
     * @param req 요청 DTO
     * @param useDefaultRegistrationDate true면 등록일 미지정 시 오늘 날짜로 설정
     */
    private Book buildBookFromRequest(AdminBookRequest req, boolean useDefaultRegistrationDate) {
        return Book.builder()
                .title(req.getTitle())
                .author(req.getAuthor())
                .publisher(req.getPublisher())
                .genre(req.getGenre())
                .tag(req.getTag())
                .coverImage(req.getCoverImageUrl())
                .price(req.getPrice())
                .registrationDate(useDefaultRegistrationDate && req.getRegistrationDate() == null
                        ? LocalDate.now()
                        : req.getRegistrationDate())
                .build();
    }
}
