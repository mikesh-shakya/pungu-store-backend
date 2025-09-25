package com.pungu.store.author_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AuthorResponse {
    private Long authorId;
    private String fullName;
    private String penName;
    private String bio;
    private String nationality;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfDeath;
    private String profilePictureUrl;

}
