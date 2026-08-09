package com.service.profile.userProfile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserProfileToRequest(
        @NotBlank(message = "name must not be blank")
        @Size(max = 255, message = "name must be at most 255 characters")
        @Pattern(regexp = "^[\\p{L}\\p{N} _\\-.,!?()]+$", message = "name contains invalid characters")
        String displayName,

        @Size(max = 2048, message = "avatarUrl must be at most 2048 characters")
        String avatarUrl,

        @Size(max = 4096, message = "bio must be at most 4096 characters")
        String bio) {
}
