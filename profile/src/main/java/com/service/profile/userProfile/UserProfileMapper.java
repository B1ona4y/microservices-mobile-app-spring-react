package com.service.profile.userProfile;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.service.profile.userProfile.dto.UserProfileToRequest;
import com.service.profile.userProfile.dto.UserProfileToResponse;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserProfileMapper {

    UserProfileToResponse toResponse(UserProfile userProfile);
    List<UserProfileToResponse> toResponseList(List<UserProfile> userProfiles);

    void update(UserProfileToRequest dto, @MappingTarget UserProfile entity);
}