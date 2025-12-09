package com.example.demo.book.repository;

import com.example.demo.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByTitleContainingOrAuthorContaining(String title, String author, Pageable pageable);
}
