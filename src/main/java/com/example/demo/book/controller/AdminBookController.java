package com.example.demo.book.controller;

import com.example.demo.book.dto.AdminBookRequest;
import com.example.demo.book.dto.AdminBookResponse;
import com.example.demo.book.dto.StockRequest;
import com.example.demo.book.dto.StockResponse;
import com.example.demo.book.entity.Book;
import com.example.demo.book.service.BookInventoryService;
import com.example.demo.book.service.BookAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController // REST API 컨트롤러 선언
@RequestMapping("/admin/books") // 관리자 도서 API 기본 경로
@RequiredArgsConstructor // final 필드에 대한 생성자 주입 자동 생성
public class AdminBookController {
    private final BookInventoryService inventoryService; // 재고 증감 처리 서비스
    private final BookAdminService bookAdminService; // 도서 생성/수정/삭제 처리 서비스

    /**
     * 관리자 재고 증감 처리
     * POST /admin/books/{bookId}/stock
     * 요청 바디: { "count": <증감 수량> }
     * 응답: 재고 변경 후 StockResponse
     */
    @PostMapping("/{bookId}/stock")
    public StockResponse restock(@PathVariable Long bookId, @RequestBody StockRequest request) {
        // count가 null이면 0으로 간주하여 재고 변동 없이 현재 재고만 조회
        int current = inventoryService.restock(bookId, request.getCount() == null ? 0 : request.getCount());
        return new StockResponse(current);
    }

    // 관리자 도서 등록: POST /admin/books
    @PostMapping
    public AdminBookResponse createBook(@RequestBody AdminBookRequest req) {
        // 요청 DTO를 Book 엔티티로 변환 (등록일 기본값은 오늘)
        Book book = buildBookFromRequest(req, true);

        Book saved = bookAdminService.createBook(book, req.getDescription());
        return new AdminBookResponse(saved.getId(), "등록완료");
    }

    // 관리자 도서 수정: PATCH /admin/books/{bookId} (부분 수정)
    @PatchMapping("/{bookId}")
    public AdminBookResponse updateBook(@PathVariable("bookId") Long bookId, @RequestBody AdminBookRequest req) {
        // 요청 DTO를 Book 엔티티로 변환 (등록일은 null이면 기존 값 유지)
        Book book = buildBookFromRequest(req, false);
        Book updated = bookAdminService.updateBook(bookId, book, req.getDescription()); // 도서/책소개 부분 수정
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
