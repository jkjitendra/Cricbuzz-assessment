package com.cricbuzz.news.service.impl;

import com.cricbuzz.news.dto.UserRequestDTO;
import com.cricbuzz.news.exception.UserAlreadyExistingException;
import com.cricbuzz.news.mapper.UserMapper;
import com.cricbuzz.news.dto.UserResponseDTO;
import com.cricbuzz.news.entity.User;
import com.cricbuzz.news.exception.ResourceNotFoundException;
import com.cricbuzz.news.repository.UserRepository;
import com.cricbuzz.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestBody) {
        Optional<User> userOptional = userRepository.findByEmail(userRequestBody.getEmail());
        if (userOptional.isEmpty()) {
            User user = User.builder()
                    .name(userRequestBody.getName())
                    .email(userRequestBody.getEmail())
                    .mobile(userRequestBody.getMobile())
                    .build();
            user.setUserCreatedAt(Instant.now());
            User savedUser = userRepository.save(user);
            return UserMapper.toResponseDto(savedUser);
        } else {
            throw new UserAlreadyExistingException("User", "email", userRequestBody.getEmail());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long userId) {
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        return UserMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        List<User> usersList = this.userRepository.findAll();
        return usersList.stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        this.userRepository.delete(user);
    }
}
