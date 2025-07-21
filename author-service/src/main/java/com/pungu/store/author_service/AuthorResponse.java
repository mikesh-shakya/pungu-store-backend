package com.pungu.store.author_service;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorResponse {
    private String name;
    private String bio;
    private String nationality;
    private String dateOfBirth;
}
