package com.example.demo.book.controller;

import com.example.demo.book.entity.Book;
import com.example.demo.book.entity.BookDetail;
import com.example.demo.book.repository.BookDetailRepository;
import com.example.demo.book.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AdminBookController 통합 테스트
 * - 도서 등록 시 Book + BookDetail(책소개) 저장 여부 확인
 * - PATCH로 부분 수정 시 Book/BookDetail 갱신 확인
 * - 삭제 시 Book/BookDetail 모두 제거 확인
 * 콘솔에 응답 JSON과 DB에 저장된 엔티티를 JSON으로 출력해 디테일을 확인할 수 있다.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 필터 비활성화
@Transactional
class AdminBookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookDetailRepository bookDetailRepository;

    @BeforeEach
    void cleanUp() {
        bookDetailRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void createBook_책소개까지_DB에_저장되고_콘솔로_확인() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "등록 도서",
                "author", "관리자",
                "publisher", "테스트출판",
                "genre", "소설",
                "tag", "테스트",
                "coverImageUrl", "https://example.com/cover.jpg",
                "price", 5000,
                "description", "등록 시 책소개"
        );

        String json = objectMapper.writeValueAsString(body);

        MvcResult result = mockMvc.perform(post("/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("등록완료"))
                .andReturn();

        System.out.println("createBook response JSON: " + result.getResponse().getContentAsString());

        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(1);
        Book saved = books.get(0);
        BookDetail detail = bookDetailRepository.findById(saved.getId()).orElseThrow();

        printBookJson("createBook saved entity", saved, detail);
    }

    @Test
    void updateBook_PATCH_일부필드와_책소개_갱신_콘솔출력() throws Exception {
        // 초기 데이터 저장
        Book book = bookRepository.save(Book.builder()
                .title("초기 제목")
                .author("초기 저자")
                .publisher("초기 출판사")
                .genre("초기 장르")
                .tag("태그")
                .coverImage("https://example.com/old.jpg")
                .price(1000)
                .registrationDate(LocalDate.of(2024, 1, 1))
                .build());

        bookDetailRepository.save(BookDetail.builder()
                .book(book)
                .description("초기 소개")
                .build());

        Map<String, Object> body = Map.of(
                "title", "수정된 제목",
                "price", 2000,
                "description", "수정된 소개"
        );

        String json = objectMapper.writeValueAsString(body);

        MvcResult result = mockMvc.perform(patch("/admin/books/{bookId}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("수정완료"))
                .andReturn();

        System.out.println("updateBook response JSON: " + result.getResponse().getContentAsString());

        Book updated = bookRepository.findById(book.getId()).orElseThrow();
        BookDetail detail = bookDetailRepository.findById(book.getId()).orElseThrow();

        printBookJson("updateBook saved entity", updated, detail);
    }

    @Test
    void deleteBook_Book과_BookDetail_모두삭제_확인() throws Exception {
        Book book = bookRepository.save(Book.builder()
                .title("삭제 대상")
                .author("저자")
                .publisher("출판사")
                .genre("장르")
                .tag("태그")
                .coverImage("https://example.com/img.jpg")
                .price(3000)
                .registrationDate(LocalDate.now())
                .build());

        BookDetail detail = bookDetailRepository.save(BookDetail.builder()
                .book(book)
                .description("삭제될 소개")
                .build());

        printBookJson("deleteBook before delete", book, detail);

        MvcResult result = mockMvc.perform(delete("/admin/books/{bookId}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("삭제완료"))
                .andReturn();

        System.out.println("deleteBook response JSON: " + result.getResponse().getContentAsString());

        boolean bookExists = bookRepository.findById(book.getId()).isPresent();
        boolean detailExists = bookDetailRepository.findById(book.getId()).isPresent();
        System.out.println("deleteBook after delete: book exists? " + bookExists + ", bookDetail exists? " + detailExists);

        assertThat(bookExists).isFalse();
        assertThat(detailExists).isFalse();
    }

    private void printBookJson(String label, Book book, BookDetail detail) throws Exception {
        Map<String, Object> savedJson = Map.of(
                "id", book.getId(),
                "title", book.getTitle(),
                "author", book.getAuthor(),
                "publisher", book.getPublisher(),
                "genre", book.getGenre(),
                "tag", book.getTag(),
                "coverImage", book.getCoverImage(),
                "price", book.getPrice(),
                "registrationDate", book.getRegistrationDate(),
                "description", detail == null ? null : detail.getDescription()
        );
        System.out.println(label + ": " + objectMapper.writeValueAsString(savedJson));
    }
}
