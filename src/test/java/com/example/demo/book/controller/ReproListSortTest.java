package com.example.demo.book.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReproListSortTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("sort=latest로 요청했을 때 500 에러가 발생하지 않아야 한다")
    void testListWithSortLatest() throws Exception {
        mockMvc.perform(get("/api/books")
                .param("page", "1")
                .param("sort", "latest"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
