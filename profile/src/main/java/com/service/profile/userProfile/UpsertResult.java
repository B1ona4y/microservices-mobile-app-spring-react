package com.service.profile.userProfile;

public record UpsertResult(UserProfile profile, Outcome outcome) {
    public enum Outcome { CREATED, UPDATED }
}
