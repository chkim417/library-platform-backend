package com.example.demo.book.service;

import com.example.demo.book.entity.Book;
import java.util.List;

public interface BookService {
    List<Book> getAllBooks();

    Book getBook(Long id);

    Book createBook(Book book);

    /**
     * 관리자가 도서를 삭제합니다. 관련된 도서관리(BookManagement) 레코드는 먼저 삭제됩니다.
     * @param id 삭제할 도서의 ID
     */
    void deleteBook(Long id);
}
