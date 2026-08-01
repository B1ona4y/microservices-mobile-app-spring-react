package com.service.profile.userProfile;

import java.util.Optional;

public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository){
        this.userProfileRepository = userProfileRepository;
    }

    public Optional<UserProfile> findByIdAndOwner(String displayName) {
        return userProfileRepository.findByDisplayName(displayName);
    }
}
