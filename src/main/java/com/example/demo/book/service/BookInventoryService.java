package com.example.demo.book.service;

import com.example.demo.book.entity.Book;
import com.example.demo.book.entity.BookManagement;
import com.example.demo.book.repository.BookManagementRepository;
import com.example.demo.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookInventoryService {
    private final BookRepository bookRepository;
    private final BookManagementRepository bookManagementRepository;

    /**
     * 도서 재고 입고 처리
     *
     * @param bookId 입고할 도서의 ID
     * @param count  입고 수량 (0 이하인 경우 현재 재고 수만 반환)
     * @return 입고 후 현재 재고(대출 가능 수)
     *
     * 동작:
     * 1) `bookId` 로 도서를 조회한다. 없으면 예외 발생.
     * 2) `count` 가 0 이하이면 현재 재고 수를 조회하여 반환한다.
     * 3) count 만큼 `BookManagement` 엔티티를 생성(대출되지 않은 상태)하여 저장한다.
     * 4) 저장 후 현재 재고 수를 반환한다.
     */
    public int restock(Long bookId, int count) {
        // 도서 존재 여부 확인 (없으면 IllegalArgumentException 발생)
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        // 요청된 입고 수량이 0이하일 경우, 변경 없이 현재 재고 수를 반환
        if (count <= 0) return bookManagementRepository.countByBookIdAndIsLoanedFalse(bookId);

        // 저장할 BookManagement 엔티티들을 수집
        List<BookManagement> toSave = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // 각각의 도서관리 레코드는 book 참조와 대출 여부(false)를 가짐
            BookManagement bm = BookManagement.builder()
                    .book(book)
                    .isLoaned(false)
                    .build();
            toSave.add(bm);
        }

        // 한 번에 저장
        bookManagementRepository.saveAll(toSave);

        // 저장 후 현재 (대출 가능) 재고 수 반환
        return bookManagementRepository.countByBookIdAndIsLoanedFalse(bookId);
    }
}