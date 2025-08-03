package com.pungu.store.author_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;
    @NotBlank(message = "Pen name is required")
    @Size(max = 100, message = "Pen name must be less than 100 characters")
    private String penName;
    @Size(max = 300, message = "Bio must be less than 300 characters")
    private String profilePictureUrl;
    @Size(max = 1000, message = "Bio must be less than 1000 characters")
    private String bio;
    @Size(max = 100, message = "Nationality must be less than 100 characters")
    private String nationality;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Date of death must be in the past")
    private LocalDate dateOfDeath;
}
