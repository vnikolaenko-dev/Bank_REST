package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setLogin(request.login());
        user.setPassword(request.password());
        return userRepository.save(user);
    }

    public Optional<User> findUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }
}
