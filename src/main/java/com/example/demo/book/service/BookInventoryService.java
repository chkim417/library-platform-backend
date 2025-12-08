package com.example.demo.book.service;

import com.example.demo.book.entity.Book;
import com.example.demo.book.entity.BookManagement;
import com.example.demo.book.repository.BookAdminRepository;
import com.example.demo.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookInventoryService {
    private final BookRepository bookRepository; // Book 조회용
    private final BookAdminRepository bookAdminRepository; // 재고(BookManagement) 조작용

    /**
     * 도서 재고 증감 처리
     *
     * @param bookId 재고를 변경할 도서 ID
     * @param count  증감 수량 (0 이하이면 재고 변경 없이 현재 수량 반환)
     * @return 변경 후 재고 수량(대출 가능 수량 기준)
     *
     * 동작:
     * 1) bookId로 도서를 조회, 없으면 예외 발생.
     * 2) count <= 0 이면 재고 변경 없이 현재 재고 반환.
     * 3) count > 0 이면 해당 수량만큼 BookManagement(대출 가능) 엔티티를 생성 후 저장.
     * 4) 최종 재고 수량을 반환.
     */
    public int restock(Long bookId, int count) {
        // 도서 존재 확인 (없으면 IllegalArgumentException)
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        // 증감 수량이 0 이하이면 재고 변경 없이 현재 재고 반환
        if (count <= 0) return bookAdminRepository.countByBookIdAndIsLoanedFalse(bookId);

        // 새로 생성할 BookManagement 엔티티들을 수집
        List<BookManagement> toSave = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // 각 재고는 해당 book을 참조하고 isLoaned=false(대출 가능)로 생성
            BookManagement bm = BookManagement.builder()
                    .book(book)
                    .isLoaned(false)
                    .build();
            toSave.add(bm);
        }

        // 한 번에 저장
        bookAdminRepository.saveAll(toSave);

        // 변경된 대출 가능 재고 수량 반환
        return bookAdminRepository.countByBookIdAndIsLoanedFalse(bookId);
    }
}
