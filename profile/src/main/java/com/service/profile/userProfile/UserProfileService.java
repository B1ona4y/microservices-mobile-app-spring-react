package com.service.profile.userProfile;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.service.profile.userProfile.dto.UserProfileToRequest;


@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper){
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    public Optional<UserProfile> findById(UUID id) {
        return userProfileRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public UserProfile upsert(UserProfileToRequest req, UUID id) {
        UserProfile userProfile = userProfileRepository.findById(id).orElseGet(() -> UserProfile.builder().id(id).build());
        userProfileMapper.update(req, userProfile);
        return userProfileRepository.save(userProfile);
    }
}
