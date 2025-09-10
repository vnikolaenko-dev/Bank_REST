package com.example.bankcards.dto.user;

import java.util.List;

public record AuthResponse(String token, List<String> roles) {
}
