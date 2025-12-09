package com.example.demo.book.controller;

import com.example.demo.book.entity.Book;
import com.example.demo.book.repository.BookManagementRepository;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 관리자 대여가능 여부(재고) 확인/증감 테스트
 * - 기본 stockcount=1로 호출 시 stockcount=1
 * - stockcount=0으로 호출 시 stockcount=0으로 리셋
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class AdminBookStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookManagementRepository bookManagementRepository;

    @BeforeEach
    void clean() {
        bookManagementRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void stockcount가_기본값일때_stockcount는_1() throws Exception {
        Book book = bookRepository.save(Book.builder()
                .title("재고 테스트")
                .author("관리자")
                .publisher("테스트출판")
                .genre("장르")
                .tag("태그")
                .coverImage("https://example.com/img.jpg")
                .price(1000)
                .registrationDate(LocalDate.now())
                .build());

        // count를 명시하지 않으면 기본 1로 처리
        MvcResult result = mockMvc.perform(post("/admin/books/{bookId}/stock", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockcount").value(1))
                .andReturn();

        System.out.println("default count response: " + result.getResponse().getContentAsString());
        assertThat(bookManagementRepository.countByBookIdAndIsLoanedFalse(book.getId())).isEqualTo(1);
    }

    @Test
    void stockcount가_0이면_stockcount는_0으로_리셋() throws Exception {
        Book book = bookRepository.save(Book.builder()
                .title("재고 리셋 테스트")
                .author("관리자")
                .publisher("테스트출판")
                .genre("장르")
                .tag("태그")
                .coverImage("https://example.com/img.jpg")
                .price(1000)
                .registrationDate(LocalDate.now())
                .build());

        // 우선 재고를 1개 만들어둔다
        mockMvc.perform(post("/admin/books/{bookId}/stock", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stockcount", 1))))
                .andExpect(status().isOk());
        assertThat(bookManagementRepository.countByBookIdAndIsLoanedFalse(book.getId())).isEqualTo(1);

        // count=0으로 호출하면 대여불가 처리로 0 반환
        MvcResult resetResult = mockMvc.perform(post("/admin/books/{bookId}/stock", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stockcount", 0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockcount").value(0))
                .andReturn();

        System.out.println("reset response: " + resetResult.getResponse().getContentAsString());
        assertThat(bookManagementRepository.countByBookIdAndIsLoanedFalse(book.getId())).isEqualTo(0);
    }
}
