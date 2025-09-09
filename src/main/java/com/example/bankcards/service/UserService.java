package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User checkUserAndPassword(String login, String password) {
        var user = findUserByUsername(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!checkPassword(user, password)) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }


    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public List<String> getUsernames() {
        return userRepository.findAll().stream().map(User::getUsername).toList();
    }

    public void deleteByUsername(String username) {
        userRepository.deleteUserByUsername(username);
    }
}
