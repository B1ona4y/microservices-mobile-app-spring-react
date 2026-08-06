package com.service.profile.error;

import java.util.UUID;

public class ProfileNotFoundException extends RuntimeException{
    public ProfileNotFoundException(UUID id) {
        super("profile not found: " + id);
    }
}
