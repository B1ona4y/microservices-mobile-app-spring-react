package com.service.profile.userProfile;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.service.profile.userProfile.dto.UserProfileToRequest;
import com.service.profile.userProfile.dto.UserProfileToResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserProfileMapper {

    UserProfileToResponse toResponse(UserProfile userProfile);
    List<UserProfileToResponse> toResponseList(List<UserProfile> userProfiles);

    @Mapping(target = "updatedAt", ignore = true)
    UserProfile toEntity(UserProfileToRequest request);
}