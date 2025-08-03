package com.pungu.store.author_service.service;

import com.pungu.store.author_service.dto.AuthorRequest;
import com.pungu.store.author_service.dto.AuthorResponse;
import com.pungu.store.author_service.exception.AuthorNotFoundException;
import com.pungu.store.author_service.exception.DuplicateAuthorException;
import com.pungu.store.author_service.model.Author;
import com.pungu.store.author_service.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing author-related business logic.
 */
@RequiredArgsConstructor
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    /**
     * Creates a new author in the system.
     *
     * @param request AuthorRequest object containing the author's details.
     * @return AuthorResponse object representing the created author.
     * @throws DuplicateAuthorException if an author with the same name already exists.
     */
    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        if (authorRepository.existsByFullNameIgnoreCase(request.getFullName())) {
            throw new DuplicateAuthorException("Author with this name already exists.");
        }

        Author author = Author.builder()
                .fullName(request.getFullName())
                .penName(request.getPenName())
                .profilePictureUrl(request.getProfilePictureUrl())
                .bio(request.getBio())
                .nationality(request.getNationality())
                .dateOfBirth(request.getDateOfBirth())
                .dateOfDeath(request.getDateOfDeath())
                .build();

        author = authorRepository.save(author);
        return mapToResponse(author);
    }

    /**
     * Retrieves a specific author by their ID.
     *
     * @param authorId ID of the author.
     * @return AuthorResponse object containing author details.
     * @throws AuthorNotFoundException if the author is not found.
     */
    public AuthorResponse getAuthorById(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        return mapToResponse(author);
    }

    /**
     * Retrieves all authors in the system.
     *
     * @return List of AuthorResponse objects representing all authors.
     */
    public List<AuthorResponse> getAllAuthors(Sort sort) {
        return authorRepository.findAll(sort).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all authors sorted by full name.
     *
     * @return Sorted list of AuthorResponse.
     */
    public List<AuthorResponse> getAuthorsSortedByName() {
        return authorRepository.findAll(Sort.by(Sort.Direction.ASC, "fullName"))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing author's details.
     *
     * @param authorId ID of the author to update.
     * @param request  New author details.
     * @return Updated AuthorResponse object.
     * @throws AuthorNotFoundException if the author is not found.
     */
    @Transactional
    public AuthorResponse updateAuthor(Long authorId, AuthorRequest request) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));

        Optional<Author> existing = authorRepository.findByFullNameIgnoreCase(request.getFullName());
        if (existing.isPresent() && !existing.get().getAuthorId().equals(authorId)) {
            throw new DuplicateAuthorException("Author with this name already exists.");
        }

        if (request.getFullName() != null) author.setFullName(request.getFullName());
        if (request.getPenName() != null) author.setPenName(request.getPenName());
        if (request.getProfilePictureUrl() != null) author.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getBio() != null) author.setBio(request.getBio());
        if (request.getNationality() != null) author.setNationality(request.getNationality());
        if (request.getDateOfBirth() != null) author.setDateOfBirth(request.getDateOfBirth());
        if (request.getDateOfDeath() != null) author.setDateOfDeath(request.getDateOfDeath());

        author = authorRepository.save(author);
        return mapToResponse(author);
    }

    /**
     * Deletes an author from the system.
     *
     * @param authorId ID of the author to delete.
     * @throws AuthorNotFoundException if the author is not found.
     */
    @Transactional
    public void deleteAuthor(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new AuthorNotFoundException("Author not found");
        }
        authorRepository.deleteById(authorId);
    }

    /**
     * Converts an Author entity to AuthorResponse DTO.
     *
     * @param author Author entity.
     * @return AuthorResponse DTO.
     */
    private AuthorResponse mapToResponse(Author author) {
        return AuthorResponse.builder()
                .authorId(author.getAuthorId())
                .fullName(author.getFullName())
                .penName(author.getPenName())
                .profilePictureUrl(author.getProfilePictureUrl())
                .bio(author.getBio())
                .nationality(author.getNationality())
                .dateOfBirth(author.getDateOfBirth())
                .dateOfDeath(author.getDateOfDeath())
                .build();
    }

    /**
     * Retrieves the name of an author by their ID.
     *
     * @param id the ID of the author
     * @return the name of the author
     * @throws AuthorNotFoundException if no author is found with the given ID
     */
    public String getAuthorNameById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        return author.getFullName();
    }

    /**
     * Retrieves the ID of an author by their name (case-insensitive).
     *
     * @param authorName the name of the author
     * @return the ID of the author
     * @throws AuthorNotFoundException if no author is found with the given name
     */
    public Long getAuthorIdByName(String authorName) {
        Author author = authorRepository.findByFullNameIgnoreCase(authorName)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        return author.getAuthorId();
    }
}
