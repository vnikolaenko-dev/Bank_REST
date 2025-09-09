package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.user.UserRequest;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/user-control")
@RequiredArgsConstructor
public class UserManipulationController {
    private final UserService userService;


    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<String>> getUsers() {
        return ResponseEntity.ok(userService.getUsernames());
    }

    @GetMapping("/delete/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteByUsername(username);
        return ResponseEntity.ok("success");
    }
}
