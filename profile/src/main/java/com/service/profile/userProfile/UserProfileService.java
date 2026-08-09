package com.service.profile.userProfile;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.service.profile.error.ProfileNotFoundException;
import com.service.profile.userProfile.UpsertResult.Outcome;
import com.service.profile.userProfile.dto.UserProfileToRequest;


@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper){
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    @Transactional(readOnly = true)
    public UserProfile findById(String id) {
        return userProfileRepository.findById(id)
            .orElseThrow(() -> new ProfileNotFoundException(id));
    }

    @Transactional
    public UpsertResult upsert(UserProfileToRequest req, String id) {
        Optional<UserProfile> found = userProfileRepository.findById(id);
        UserProfile userProfile = found.orElseGet(() -> UserProfile.builder().id(id).build());
        userProfileMapper.update(req, userProfile);
        UserProfile saved = userProfileRepository.save(userProfile);
        return new UpsertResult(saved, found.isPresent() ? Outcome.UPDATED : Outcome.CREATED);
    }
}
