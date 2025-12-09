package com.example.demo.book.service;

import com.example.demo.book.dto.BookListResponse;
import com.example.demo.book.dto.BookResponse;
// Note: BookRequest is removed as it's only used by non-Read methods
import com.example.demo.book.entity.Book;
import com.example.demo.book.entity.BookDetail;
import com.example.demo.book.repository.BookDetailRepository;
import com.example.demo.book.repository.BookManagementRepository;
import com.example.demo.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

        private final BookRepository bookRepository;
        private final BookDetailRepository bookDetailRepository;
        private final BookManagementRepository bookManagementRepository;

        // ======================================================
        // 1. 도서 목록 및 검색 조회 (Controller의 GET /api/books 지원)
        // ======================================================
        @Transactional(readOnly = true)
        public BookListResponse getAllBooks(Integer page, String sort, String keyword) {
                // 0-based index 처리
                int pageNum = (page != null && page > 0) ? page - 1 : 0;
                Sort sortObj = Sort.by("id");
                if ("latest".equals(sort)) {
                        sortObj = Sort.by("registrationDate").descending();
                } else if (sort != null && !sort.isEmpty()) {
                        sortObj = Sort.by(sort);
                }

                // Spring Data JPA를 사용한 페이지네이션/정렬
                Pageable pageable = PageRequest.of(pageNum, 10, sortObj);
                Page<Book> bookPage;

                if (keyword != null && !keyword.isEmpty()) {
                        bookPage = bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword, pageable);
                } else {
                        bookPage = bookRepository.findAll(pageable);
                }

                List<BookResponse> bookResponses = bookPage.getContent().stream()
                                .map(book -> mapToResponse(book, false)) // 목록 조회 시 상세 정보 제외
                                .collect(Collectors.toList());

                // API 명세에 따른 BookListResponse 구성 (count 포함)
                return BookListResponse.builder()
                                .count(bookPage.getTotalElements())
                                .books(bookResponses)
                                .build();
        }

        // ======================================================
        // 2. 도서 상세 조회 (Controller의 GET /api/books/{bookId} 지원)
        // ======================================================
        @Transactional(readOnly = true)
        public BookResponse getBookById(Long bookNo) {
                Book book = bookRepository.findById(bookNo)
                                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookNo));

                // DTO 변환 및 반환
                return mapToResponse(book);
        }

        // ======================================================
        // 4. 헬퍼 메서드: DTO 변환
        // ======================================================
        private BookResponse mapToResponse(Book book) {
                // 기본값: 상세 정보 포함 (기존 코드 호환성을 위해)
                return mapToResponse(book, true);
        }

        private BookResponse mapToResponse(Book book, boolean includeDetail) {
                String description = null;

                if (includeDetail) {
                        description = bookDetailRepository.findById(book.getId())
                                        .map(BookDetail::getDescription)
                                        .orElse(null);
                }

                String genre = book.getGenre();
                String tag = book.getTag();
                // 대출 가능 여부 확인: 대출되지 않은(isLoaned=false) 책이 하나라도 있으면 "대출중 아님(false)"
                boolean isAvailable = bookManagementRepository.findFirstByBookIdAndIsLoanedFalse(book.getId())
                                .isPresent();
                Boolean isLoaned = !isAvailable;

                return BookResponse.builder()
                                .bookNo(book.getId())
                                .title(book.getTitle())
                                .author(book.getAuthor())
                                .publisher(book.getPublisher())
                                .coverImageUrl(book.getCoverImage())
                                .price(book.getPrice())
                                .registerDate(book.getRegistrationDate())
                                .description(description) // includeDetail이 false면 null
                                .genre(genre)
                                .tag(tag)
                                .isLoaned(isLoaned)
                                .build();
        }
        // 원래 있던 bookdelete, insert부분 태민님이 분리해서 다른 파일로 분리했습니다.
}
