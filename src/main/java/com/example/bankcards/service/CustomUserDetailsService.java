package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Находим пользователя по логину
        var user = userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));

        // Преобразуем роли пользователя в GrantedAuthority
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // "ROLE_ADMIN", "ROLE_USER"
                .collect(Collectors.toList());

        // Возвращаем объект UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                authorities
        );
    }
}

