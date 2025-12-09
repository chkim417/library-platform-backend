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

    private final BookRepository bookRepository; // Book 조회용
    private final BookManagementRepository bookManagementRepository; // 재고(BookManagement) 조작용

    /**
     * 도서 재고 증감 처리
     *
     * @param bookId 재고를 변경할 도서 ID
     * @param count  증감 수량 (0 이하이면 재고 변경 없이 현재 수량 반환)
     * @return 변경 후 재고 수량(대출 가능 수량 기준)
     */
    public int restock(Long bookId, int count) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        if (count <= 0) return bookManagementRepository.countByBookIdAndIsLoanedFalse(bookId);

        List<BookManagement> toSave = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BookManagement bm = BookManagement.builder()
                    .book(book)
                    .isLoaned(false)
                    .build();
            toSave.add(bm);
        }

        bookManagementRepository.saveAll(toSave);

        return bookManagementRepository.countByBookIdAndIsLoanedFalse(bookId);
    }
}
