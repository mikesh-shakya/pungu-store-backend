package com.pungu.store.auth_service.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pungu.store.auth_service.entities.Role;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for returning user details in responses.
 * Excludes sensitive fields like password.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String firstName;
    private String lastName;

    private String email;
    private Role role;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private int age; // Age is a derived field and may be calculated at the service layer.

    private String profilePictureUrl;
    private String nationality;

}
