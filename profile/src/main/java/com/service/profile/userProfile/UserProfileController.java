package com.service.profile.userProfile;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.service.profile.userProfile.dto.UserProfileToRequest;
import com.service.profile.userProfile.dto.UserProfileToResponse;
@RestController
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final UserProfileMapper userProfileMapper;

    public UserProfileController(UserProfileService userProfileService, UserProfileMapper userProfileMapper) {
        this.userProfileService = userProfileService;
        this.userProfileMapper = userProfileMapper;
    }

    @GetMapping("/profile/me")
    public ResponseEntity<UserProfileToResponse> me(@AuthenticationPrincipal Jwt jwt) {
        return userProfileService.findById(UUID.fromString(jwt.getSubject()))
                .map(userProfileMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileToResponse> getOrCreateUserProfile(@RequestBody UserProfileToRequest req, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UserProfile saved = userProfileService.upsert(req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfileMapper.toResponse(saved));
    }
}
