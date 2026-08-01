package com.service.profile.userProfile.dto;

import java.util.UUID;

public record UserProfileToRequest(UUID id, String displayName, String avatarUrl, String bio, long version) {

}
