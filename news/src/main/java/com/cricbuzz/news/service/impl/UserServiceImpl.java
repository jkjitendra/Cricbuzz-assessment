package com.cricbuzz.news.service.impl;

import com.cricbuzz.news.dto.UserRequestDTO;
import com.cricbuzz.news.exception.UserAlreadyExistingException;
import com.cricbuzz.news.mapper.UserMapper;
import com.cricbuzz.news.dto.UserResponseDTO;
import com.cricbuzz.news.entity.User;
import com.cricbuzz.news.exception.ResourceNotFoundException;
import com.cricbuzz.news.repository.UserRepository;
import com.cricbuzz.news.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestBody) {
        logger.info("Creating user with email: {}", userRequestBody.getEmail());
        try {
            Optional<User> userOptional = userRepository.findByEmail(userRequestBody.getEmail());
            if (userOptional.isEmpty()) {
                User user = User.builder()
                        .name(userRequestBody.getName())
                        .email(userRequestBody.getEmail())
                        .mobile(userRequestBody.getMobile())
                        .build();
                user.setUserCreatedAt(Instant.now());
                User savedUser = userRepository.save(user);
                logger.info("User created successfully with ID: {}", savedUser.getId());
                return UserMapper.toResponseDto(savedUser);
            } else {
                throw new UserAlreadyExistingException("User", "email", userRequestBody.getEmail());
            }
        } catch (UserAlreadyExistingException e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating user: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while creating user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long userId) {
        logger.info("Fetching user with ID: {}", userId);
        try {
            User user = this.userRepository
                    .findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
            logger.info("User fetched successfully with ID: {}", userId);
            return UserMapper.toResponseDto(user);
        } catch (ResourceNotFoundException e) {
            logger.error("Error fetching user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error fetching user: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Fetching all users");
        try {
            List<User> usersList = this.userRepository.findAll();
            logger.info("Fetched {} users", usersList.size());
            return usersList.stream()
                    .map(UserMapper::toResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Unexpected error fetching users: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching users", e);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        try {
            User user = this.userRepository
                    .findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
            this.userRepository.delete(user);
            logger.info("User deleted successfully with ID: {}", userId);
        } catch (ResourceNotFoundException e) {
            logger.error("Error deleting user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting user: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while deleting user", e);
        }
    }
}
