package com.example.bankcards.service;

import com.example.bankcards.controller.AuthenticationController;
import com.example.bankcards.dto.user.AuthRequest;
import com.example.bankcards.dto.user.UserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CardService cardService;

    public User login(AuthRequest request) {
        // находим пользователя
        User user = findUserByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // проверяем пароль
        try {
            if (!request.password().equals(CryptoUtil.decrypt(user.getPassword()))) {
                throw new RuntimeException("Invalid credentials");
            }

            // проверка карт, не вышел ли срок использования
            Thread thread = new Thread(() -> {
                cardService.checkCards(user);
            });

            thread.start();
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        try {
            user.setPassword(CryptoUtil.encrypt(request.password()));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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
        try {
            return rawPassword.equals(CryptoUtil.decrypt(user.getPassword()));
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getUsernames() {
        return userRepository.findAll().stream().map(User::getUsername).toList();
    }

    public void deleteByUsername(String username) {
        userRepository.deleteUserByUsername(username);
    }
}
