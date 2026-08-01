package com.service.profile.userProfile;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

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

    public Optional<UserProfile> createProfile(UUID id, String name) {
        if (userProfileRepository.findById(id).isPresent()) {
            return Optional.empty();
        }
        UserProfile userProfile = UserProfile.builder()
                .id(id)
                .displayName(name)
                .build();
        return Optional.of(userProfileRepository.save(userProfile));
    }

    public Optional<UserProfile> findByIdAndOwner(String displayName) {
        return userProfileRepository.findByDisplayName(displayName);
    }
}
