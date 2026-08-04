package com.service.profile.userProfile;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.service.profile.userProfile.dto.UserProfileToRequest;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository){
        this.userProfileRepository = userProfileRepository;
    }

    public Optional<UserProfile> findById(UUID id) {
        return userProfileRepository.findById(id);
    }

    public UserProfile save(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    public Optional<UserProfile> createIfAbsent(UserProfileToRequest req, UUID id) {
        if (userProfileRepository.existsById(id)) {
            return Optional.empty();
        }
        UserProfile userProfile = UserProfile.builder()
                .id(id)
                .displayName(req.displayName())
                .bio(req.bio())
                .avatarUrl(req.avatarUrl())
                .build();
        return Optional.of(userProfileRepository.save(userProfile));
    }

    public Optional<UserProfile> findByIdAndOwner(String displayName) {
        return userProfileRepository.findByDisplayName(displayName);
    }
}
