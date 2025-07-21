package com.pungu.store.author_service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorRequest {

    @NotBlank(message = "Author name is required")
    @Size(max = 100, message = "Author name must be less than 100 characters")
    private String name;
    private String bio;
    private String nationality;
    private String dateOfBirth;  // Will be converted to LocalDateTime
}
