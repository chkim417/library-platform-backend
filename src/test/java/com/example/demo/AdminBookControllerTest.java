package com.example.demo;

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
 * AdminBookController 동작을 검증하는 통합 테스트.
 *
 * 시나리오
 * 1) 도서 등록 시 Book + BookDetail(책소개) 함께 저장
 * 2) PATCH로 도서 일부 필드와 책소개를 수정
 * 3) 삭제 시 Book, BookDetail 함께 제거
 *
 * 각 단계에서 응답 JSON과 DB 저장 상태를 콘솔에 찍어 상세 확인 가능.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 필터 비활성화하여 인증 없이 테스트
@Transactional
class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookDetailRepository bookDetailRepository;

    @BeforeEach
    void setUp() {
        // 매 테스트 시작 전 깔끔한 상태를 위해 모두 삭제
        bookDetailRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void createBook_책소개까지_함께_저장된다() throws Exception {
        // given: 책소개를 포함한 등록 요청 본문
        Map<String, Object> body = Map.of(
                "title", "테스트 도서",
                "author", "관리자",
                "publisher", "테스트출판",
                "genre", "소설",
                "tag", "테스트",
                "coverImageUrl", "https://example.com/cover.jpg",
                "price", 5000,
                "description", "이것은 책소개입니다."
        );

        String json = objectMapper.writeValueAsString(body);

        // when: 등록 호출
        MvcResult result = mockMvc.perform(post("/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("등록완료"))
                .andReturn();

        // then: 응답 JSON 로그
        System.out.println("createBook response JSON: " + result.getResponse().getContentAsString());

        // and: DB에 Book/BookDetail이 함께 저장됐는지 검증
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(1);
        Book saved = books.get(0);
        BookDetail detail = bookDetailRepository.findById(saved.getId()).orElseThrow();

        printBookJson("createBook saved entity JSON", saved, detail);
    }

    @Test
    void updateBook_PATCH_부분수정과_책소개_갱신() throws Exception {
        // given: 초기 Book/BookDetail 저장
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

        // 일부 필드만 수정하는 PATCH 요청 본문
        Map<String, Object> body = Map.of(
                "title", "수정된 제목",   // 변경
                "price", 2000,          // 변경
                "description", "수정된 소개" // 변경
        );

        String json = objectMapper.writeValueAsString(body);

        // when: PATCH 호출
        MvcResult result = mockMvc.perform(patch("/admin/books/{bookId}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("수정완료"))
                .andReturn();

        // then: 응답 JSON 로그
        System.out.println("updateBook response JSON: " + result.getResponse().getContentAsString());

        // and: 변경 필드/유지 필드/책소개 갱신 여부 검증
        Book updated = bookRepository.findById(book.getId()).orElseThrow();
        BookDetail detail = bookDetailRepository.findById(book.getId()).orElseThrow();
        printBookJson("updateBook saved entity JSON", updated, detail);
    }

    @Test
    void deleteBook_도서와_책소개_모두삭제() throws Exception {
        // given: 삭제 대상 Book/BookDetail 저장
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

        // 삭제 전 상태 출력
        printBookJson("deleteBook before delete JSON", book, detail);

        // when: DELETE 호출
        MvcResult result = mockMvc.perform(delete("/admin/books/{bookId}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("삭제완료"))
                .andReturn();

        // then: 응답 JSON 로그
        System.out.println("deleteBook response JSON: " + result.getResponse().getContentAsString());

        // and: 삭제 후 존재 여부 확인 + 로그
        boolean bookExists = bookRepository.findById(book.getId()).isPresent();
        boolean detailExists = bookDetailRepository.findById(book.getId()).isPresent();
        System.out.println("deleteBook after delete: book exists? " + bookExists + ", bookDetail exists? " + detailExists);

        assertThat(bookExists).isFalse();
        assertThat(detailExists).isFalse();
    }

    /**
        * Book과 BookDetail 상태를 JSON으로 직렬화해 콘솔에 출력.
        * description이 null이면 BookDetail이 없음을 의미.
        */
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
