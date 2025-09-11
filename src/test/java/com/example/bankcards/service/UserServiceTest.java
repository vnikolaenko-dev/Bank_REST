package com.example.bankcards.service;

import com.example.bankcards.dto.user.AuthRequest;
import com.example.bankcards.dto.user.UserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private UserService userService;

    @Test
    void login() throws Exception {
        String username = "test-user";
        String rawPassword = "test-password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(CryptoUtil.encrypt(rawPassword)); // зашифрованный пароль

        AuthRequest request = new AuthRequest(username, rawPassword);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.login(request);

        assert result != null : "User should not be null";
        assert result.getUsername().equals(username) : "Username should match";
    }

    @Test
    void login_invalidPassword() throws Exception {
        String username = "test-user";
        String rawPassword = "test-password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(CryptoUtil.encrypt("other-password")); // другой пароль

        AuthRequest request = new AuthRequest(username, rawPassword);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        boolean thrown = false;
        try {
            userService.login(request);
        } catch (RuntimeException e) {
            thrown = true;
            assert e.getMessage().equals("Invalid credentials") : "Should throw invalid credentials";
        }
        assert thrown : "Exception should be thrown for wrong password";
    }

    @Test
    void login_userNotFound() throws Exception {
        String username = "missing-user";
        AuthRequest request = new AuthRequest(username, "pass");

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        boolean thrown = false;
        try {
            userService.login(request);
        } catch (RuntimeException e) {
            thrown = true;
            assert e.getMessage().equals("User not found") : "Should throw user not found";
        }
        assert thrown : "Exception should be thrown for missing user";
    }

    @Test
    void createUser_success() throws Exception {
        String username = "new-user";
        String password = "pass123";
        UserRequest request = new UserRequest(username, password);

        // Сохраняем юзера через мок
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(request);

        // Проверяем, что пользователь сохранен с правильным username
        verify(userRepository).save(argThat(user -> {
            assert user.getUsername().equals(username) : "Username should match";
            assert user.getPassword() != null : "Password should be encrypted";
            return true;
        }));
    }

    @Test
    void findUserByUsername_success() {
        String username = "user1";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByUsername(username);

        assert result.isPresent() : "User should be present";
        assert result.get().getUsername().equals(username) : "Username should match";
    }

    @Test
    void checkUserAndPassword_success() throws Exception {
        String username = "user1";
        String password = "pass";
        User user = new User();
        user.setUsername(username);
        user.setPassword(CryptoUtil.encrypt(password));

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.checkUserAndPassword(username, password);

        assert result != null : "User should not be null";
        assert result.getUsername().equals(username) : "Username should match";
    }

    @Test
    void checkUserAndPassword_invalidPassword() throws Exception {
        String username = "user1";
        String password = "pass";
        User user = new User();
        user.setUsername(username);
        user.setPassword(CryptoUtil.encrypt("wrong-pass"));

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        boolean thrown = false;
        try {
            userService.checkUserAndPassword(username, password);
        } catch (RuntimeException e) {
            thrown = true;
            assert e.getMessage().equals("Invalid password") : "Should throw invalid password";
        }
        assert thrown : "Exception should be thrown for wrong password";
    }

    @Test
    void getUsernames_success() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<String> usernames = userService.getUsernames();

        assert usernames.size() == 2 : "Should return 2 usernames";
        assert usernames.contains("user1") : "Contains user1";
        assert usernames.contains("user2") : "Contains user2";
    }

    @Test
    void deleteByUsername_success() {
        String username = "user1";

        userService.deleteByUsername(username);

        verify(userRepository).deleteUserByUsername(username);
    }
}
