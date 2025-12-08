package com.example.demo.book.service;

import com.example.demo.book.entity.Book;
import com.example.demo.book.repository.BookManagementRepository;
import com.example.demo.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookManagementRepository bookManagementRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        // 존재 확인
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found");
        }

        // 연관된 BookManagement 레코드 삭제 (FK 제약 회피)
        bookManagementRepository.deleteByBookId(id);

        // 도서 삭제
        bookRepository.deleteById(id);
    }
}
