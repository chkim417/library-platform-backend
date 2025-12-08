package com.example.demo;

import com.example.demo.book.entity.Book;
import com.example.demo.book.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void createBookRegistersNewBook() throws Exception {
        // given: 요청 바디 준비
        Map<String, Object> body = Map.of(
                "title", "신규 도서",
                "author", "관리자",
                "publisher", "테스트출판",
                "price", 2000,
                "coverImageUrl", "https://example.com/cover.jpg",
                "genre", "테스트",
                "tag", "신간"
        );

        String json = objectMapper.writeValueAsString(body);

        // when: 도서 등록 요청
        mockMvc.perform(post("/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                // then: 응답 메시지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("등록완료"));

        // and: DB에 도서가 저장되었는지 확인
        List<Book> all = bookRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("신규 도서", all.get(0).getTitle());
    }
}