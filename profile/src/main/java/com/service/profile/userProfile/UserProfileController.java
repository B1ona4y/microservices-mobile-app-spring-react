package com.service.profile.userProfile;

import java.util.UUID;

import java.util.Optional;
import org.springframework.boot.security.autoconfigure.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/profile")
    public ResponseEntity<UserProfileToResponse> createProfile(@RequestBody UserProfileToRequest req, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        Optional<UserProfile> existing = userProfileService.findById(userId);

        if (existing.isPresent()) {
            return ResponseEntity.ok(userProfileMapper.toResponse(existing.get()));
        }

        UserProfile entity = userProfileMapper.toEntity(req, userId);
        UserProfile saved = userProfileService.save(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(userProfileMapper.toResponse(saved));
    }
}
