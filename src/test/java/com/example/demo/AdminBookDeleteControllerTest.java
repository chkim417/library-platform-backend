package com.example.demo;

import com.example.demo.book.entity.Book;
import com.example.demo.book.entity.BookManagement;
import com.example.demo.book.repository.BookManagementRepository;
import com.example.demo.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "OPENAI_API_KEY=dummy-key-for-test")
@AutoConfigureMockMvc
@Transactional
public class AdminBookDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookManagementRepository bookManagementRepository;

    @BeforeEach
    void setUp() {
        bookManagementRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void deleteBookRemovesBookAndManagementRecords() throws Exception {
        // given: 도서와 연관된 BookManagement 레코드 2개
        Book book = Book.builder()
                .title("삭제 테스트 도서")
                .publisher("테스트 출판사")
                .author("저자")
                .genre("테스트")
                .tag("tag")
                .coverImage(null)
                .price(1500)
                .registrationDate(LocalDate.now())
                .build();
        book = Objects.requireNonNull(bookRepository.save(book));

        BookManagement bm1 = BookManagement.builder().book(book).isLoaned(false).build();
        BookManagement bm2 = BookManagement.builder().book(book).isLoaned(false).build();
        bookManagementRepository.save(bm1);
        bookManagementRepository.save(bm2);

        Long id = Objects.requireNonNull(book.getId());

        // when: 삭제 요청
        mockMvc.perform(delete("/admin/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("삭제완료"))
                .andExpect(jsonPath("$.bookId").value(id.intValue()));

        // then: DB에서 도서와 연관 레코드가 삭제되었는지 확인
        assertFalse(bookRepository.existsById(id));
        assertTrue(bookManagementRepository.findByBookIdAndIsLoanedFalse(id).isEmpty());
    }
}