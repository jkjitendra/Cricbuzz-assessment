package com.cricbuzz.news.mapper;

import com.cricbuzz.news.dto.UserRequestDTO;
import com.cricbuzz.news.dto.UserResponseDTO;
import com.cricbuzz.news.entity.User;

import java.time.Instant;

public class UserMapper {

    public static UserResponseDTO toResponseDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setUserCreatedAt(user.getUserCreatedAt());
        return dto;
    }

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setUserCreatedAt(Instant.now());
        return user;
    }
}
