package com.pungu.store.book_service.repositories;

import com.pungu.store.book_service.entities.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
        List<Book> findByAuthorId(Long authorId, Sort sort);
        boolean existsById(Long bookId);

}
