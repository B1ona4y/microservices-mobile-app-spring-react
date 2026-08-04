package com.service.profile.userProfile.dto;

import com.service.profile.userProfile.UserProfile;

public record UpsertResult(UserProfile profile, Outcome outcome) {
    public enum Outcome { CREATED, UPDATED }
}
