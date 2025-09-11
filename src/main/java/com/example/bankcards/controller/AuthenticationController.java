package com.example.bankcards.controller;

import com.example.bankcards.dto.user.AuthRequest;
import com.example.bankcards.dto.user.AuthResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtUtil;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = userService.login(request);

        // Получаем роли пользователя
        var roles = user.getRoles().stream()
                .map(r -> r.getName()) // "ROLE_ADMIN", "ROLE_USER"
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getUsername(), roles);
        return new AuthResponse(token, roles);
    }
}
