package com.pungu.store.auth_service.controller;

import com.pungu.store.auth_service.dtos.UserRegistrationRequest;
import com.pungu.store.auth_service.entities.Users;
import com.pungu.store.auth_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling user-related HTTP requests.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<List<UserRegistrationRequest>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<Users> updateUserRole(@PathVariable Long userId, @RequestBody String role) {
        Users updateRole = this.userService.updateRole(userId, role);
        return new ResponseEntity<>(updateRole, HttpStatus.OK);
    }
}
