package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user-control")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        try {
            userService.createUser(request);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("error");
        }

    }
}
