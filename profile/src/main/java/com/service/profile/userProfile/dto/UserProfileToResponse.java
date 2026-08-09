package com.service.profile.userProfile.dto;

import java.time.Instant;

public record UserProfileToResponse(
    String id,
    String displayName,
    String avatarUrl,
    String bio,
    long version,
    Instant updatedAt
) {}
