package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public record AuthRequest(String username, String password) {}
    public record AuthResponse(String token, List<String> roles) {}

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        // Находим пользователя
        User user = userRepository.findUserByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверяем пароль
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Получаем роли пользователя
        var roles = user.getRoles().stream()
                .map(r -> r.getName()) // "ROLE_ADMIN", "ROLE_USER"
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getUsername(), roles);
        return new AuthResponse(token, roles);
    }
}
