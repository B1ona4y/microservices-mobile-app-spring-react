package com.service.profile.userProfile.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileToResponse(UUID id, String displayName, String avatarUrl, long version, Instant updatedAt) {

}
