package com.service.profile.error;

public class ProfileNotFoundException extends RuntimeException{
    public ProfileNotFoundException(String id) {
        super("profile not found: " + id);
    }
}
