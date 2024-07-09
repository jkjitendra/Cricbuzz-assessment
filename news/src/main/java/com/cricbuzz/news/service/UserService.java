package com.cricbuzz.news.service;

import com.cricbuzz.news.dto.UserRequestDTO;
import com.cricbuzz.news.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO user);

    UserResponseDTO getUserById(Long userId);

    List<UserResponseDTO> getAllUsers();

    void deleteUser(Long userId);

}
