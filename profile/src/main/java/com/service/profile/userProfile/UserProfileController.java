package com.service.profile.userProfile;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.profile.userProfile.dto.UpsertResult;
import com.service.profile.userProfile.dto.UserProfileToRequest;
import com.service.profile.userProfile.dto.UserProfileToResponse;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/profiles")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final UserProfileMapper userProfileMapper;

    public UserProfileController(UserProfileService userProfileService, UserProfileMapper userProfileMapper) {
        this.userProfileService = userProfileService;
        this.userProfileMapper = userProfileMapper;
    }

    private UUID currentUserId(Jwt jwt) {
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            throw new InvalidBearerTokenException("malformed subject");
        }
    }

    @GetMapping("/me")
    public UserProfileToResponse me(@AuthenticationPrincipal Jwt jwt) {
        return userProfileMapper.toResponse(userProfileService.findById(currentUserId(jwt)));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileToResponse> getOrCreateUserProfile(@Valid @RequestBody UserProfileToRequest req, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UpsertResult result = userProfileService.upsert(req, userId);
        HttpStatus status = switch (result.outcome()) {
            case CREATED -> HttpStatus.CREATED;
            case UPDATED -> HttpStatus.OK;
        };
        return ResponseEntity.status(status).body(userProfileMapper.toResponse(result.profile()));
    }


}
