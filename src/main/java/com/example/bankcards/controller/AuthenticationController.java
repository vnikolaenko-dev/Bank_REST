package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // DTO для запроса логина
    public record AuthRequest(String login, String password) {}

    // DTO для ответа с токеном
    public record AuthResponse(String token) {}

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        // Находим пользователя
        User user = userRepository.findUserByLogin(request.login())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверяем пароль
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Получаем роли пользователя
        var roles = user.getRoles().stream()
                .map(r -> r.getName()) // "ROLE_ADMIN", "ROLE_USER"
                .collect(Collectors.toList());

        // Генерируем JWT
        String token = jwtUtil.generateToken(user.getLogin(), roles);

        return new AuthResponse(token);
    }
}
