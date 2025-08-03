package com.pungu.store.auth_service.dtos;

import com.pungu.store.auth_service.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserMapper {
    public UserResponseDTO convertUserToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .dateOfBirth(user.getDateOfBirth())
                .age(calculateAge(user.getDateOfBirth()))
                .profilePictureUrl(user.getProfilePictureUrl())
                .nationality(user.getNationality())
                .build();
    }

    private int calculateAge(LocalDate dob) {
        return (dob != null) ? Period.between(dob, LocalDate.now()).getYears() : 0;
    }
}
