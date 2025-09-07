package com.example.bankcards.util;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        if (userRepository.findUserByLogin("admin").isEmpty()) {
            User admin = new User();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.getRoles().add(adminRole);
            admin.getRoles().add(userRole);
            userRepository.save(admin);
            System.out.println("Admin user created: admin / admin123");
        }
    }
}
