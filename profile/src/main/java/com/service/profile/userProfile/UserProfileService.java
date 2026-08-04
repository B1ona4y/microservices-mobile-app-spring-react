package com.service.profile.userProfile;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.service.profile.userProfile.dto.UpsertResult;
import com.service.profile.userProfile.dto.UserProfileToRequest;
import com.service.profile.userProfile.dto.UpsertResult.Outcome;


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
    public UpsertResult upsert(UserProfileToRequest req, UUID id) {
        Optional<UserProfile> found = userProfileRepository.findById(id);
        UserProfile userProfile = found.orElseGet(() -> UserProfile.builder().id(id).build());
        userProfileMapper.update(req, userProfile);
        UserProfile saved = userProfileRepository.save(userProfile);
        return new UpsertResult(saved, found.isPresent() ? Outcome.UPDATED : Outcome.CREATED);
    }
}
