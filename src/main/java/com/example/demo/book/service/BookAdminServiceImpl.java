package com.example.demo.book.service;

import com.example.demo.book.entity.Book;
import com.example.demo.book.entity.BookDetail;
import com.example.demo.book.repository.BookDetailRepository;
import com.example.demo.book.repository.BookManagementRepository;
import com.example.demo.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookAdminServiceImpl implements BookAdminService {

    private final BookRepository bookRepository; // Book CRUD
    private final BookDetailRepository bookDetailRepository; // BookDetail CRUD
    private final BookManagementRepository bookManagementRepository; // 재고(BookManagement) CRUD

    @Override
    public Book createBook(Book book, String description) {
        Book saved = bookRepository.save(book);
        // 책소개가 비어있지 않으면 BookDetail도 함께 저장 (@MapsId)
        if (description != null && !description.isBlank()) {
            BookDetail detail = BookDetail.builder()
                    .book(saved)
                    .description(description)
                    .build();
            bookDetailRepository.save(detail);
        }
        return saved;
    }

    @Override
    public Book updateBook(Long bookId, Book updated, String description) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다: " + bookId));

        // null이 아닌 필드만 업데이트
        if (updated.getTitle() != null) book.setTitle(updated.getTitle());
        if (updated.getAuthor() != null) book.setAuthor(updated.getAuthor());
        if (updated.getPublisher() != null) book.setPublisher(updated.getPublisher());
        if (updated.getGenre() != null) book.setGenre(updated.getGenre());
        if (updated.getTag() != null) book.setTag(updated.getTag());
        if (updated.getCoverImage() != null) book.setCoverImage(updated.getCoverImage());
        if (updated.getPrice() != null) book.setPrice(updated.getPrice());
        if (updated.getRegistrationDate() != null) book.setRegistrationDate(updated.getRegistrationDate());

        Book saved = bookRepository.save(book);

        // description이 null이 아니면 소개 처리 (null이면 기존 유지)
        if (description != null) {
            if (description.isBlank()) {
                bookDetailRepository.findById(bookId).ifPresent(bookDetailRepository::delete);
            } else {
                BookDetail detail = bookDetailRepository.findById(bookId)
                        .orElseGet(() -> BookDetail.builder()
                                .book(saved)
                                .build());
                detail.setDescription(description);
                bookDetailRepository.save(detail);
            }
        }
        return saved;
    }

    @Override
    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("해당 도서를 찾을 수 없습니다: " + bookId);
        }
        // FK 제약 방지를 위해 연관 데이터 먼저 삭제
        bookDetailRepository.findById(bookId).ifPresent(bookDetailRepository::delete);
        bookManagementRepository.deleteByBookId(bookId);
        bookRepository.deleteById(bookId);
    }
}
