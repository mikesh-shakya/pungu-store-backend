package com.pungu.store.author_service.repository;

import com.pungu.store.author_service.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByFullNameIgnoreCase(String fullName);
    boolean existsByFullNameIgnoreCase(String fullName);
}
