// src/main/java/com/example/HMS/mapper/UserMapper.java

package com.example.HMS.mapper;

import com.example.HMS.dto.response.UserResponse;
import com.example.HMS.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "profilePicture", source = "profilePicture")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "authProvider", source = "authProvider")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "locked", source = "locked")
    @Mapping(target = "lastLogin", source = "lastLogin")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponse toResponse(User user);
}