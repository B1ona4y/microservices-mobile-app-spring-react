package com.example.oauth2.user.dto;

import java.util.UUID;

public record UserResponse(UUID id, String email, String name) {
}
