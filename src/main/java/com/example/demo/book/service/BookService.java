package com.example.demo.book.service;

import com.example.demo.book.entity.Book;
import java.util.List;

public interface BookService {
    List<Book> getAllBooks();

    Book getBook(Long id);

    Book createBook(Book book);
}
