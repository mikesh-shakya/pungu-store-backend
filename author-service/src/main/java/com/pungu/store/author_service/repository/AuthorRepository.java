package com.pungu.store.author_service.repository;

import com.pungu.store.author_service.model.Author;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Slice<Author> findAllBy(Pageable pageable);

    Optional<Author> findByFullNameIgnoreCase(String fullName);

    boolean existsByFullNameIgnoreCase(String fullName);

    Slice<Author> findByFullNameStartingWithIgnoreCaseOrPenNameStartingWithIgnoreCase(String fullNamePrefix, String penNamePrefix, Pageable pageable);

}
