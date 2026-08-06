package com.service.profile.userProfile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID>{
}
