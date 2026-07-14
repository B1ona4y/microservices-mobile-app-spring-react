package com.example.oauth2.user;

import org.mapstruct.Mapper;

import com.example.oauth2.user.dto.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

}
