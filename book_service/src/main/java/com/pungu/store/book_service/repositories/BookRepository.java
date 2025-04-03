package com.pungu.store.book_service.repositories;

import com.pungu.store.book_service.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
