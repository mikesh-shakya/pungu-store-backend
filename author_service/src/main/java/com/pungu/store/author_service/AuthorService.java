package com.pungu.store.author_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = Author.builder()
                .name(request.getName())
                .bio(request.getBio())
                .nationality(request.getNationality())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth()))
                .createdAt(LocalDate.now())
                .lastUpdatedAt(LocalDate.now())
                .build();
        author = authorRepository.save(author);
        return mapToResponse(author);
    }

    public AuthorResponse getAuthorById(Long authorId) {
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException("Author not found"));
        return mapToResponse(author);
    }

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AuthorResponse updateAuthor(Long authorId, AuthorRequest request) {
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException("Author not found"));

        author.setName(request.getName());
        author.setBio(request.getBio());
        author.setNationality(request.getNationality());
        author.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        author.setLastUpdatedAt(LocalDate.now());

        author = authorRepository.save(author);
        return mapToResponse(author);
    }

    public void deleteAuthor(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new RuntimeException("Author not found");
        }
        authorRepository.deleteById(authorId);
    }

    private AuthorResponse mapToResponse(Author author) {
        return AuthorResponse.builder()
                .name(author.getName())
                .bio(author.getBio())
                .nationality(author.getNationality())
                .dateOfBirth(author.getDateOfBirth().toString())
                .build();
    }
}
