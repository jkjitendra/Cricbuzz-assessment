package com.cricbuzz.news.service;

import com.cricbuzz.news.dto.UserRequestDTO;
import com.cricbuzz.news.dto.UserResponseDTO;
import com.cricbuzz.news.entity.User;
import com.cricbuzz.news.exception.ResourceNotFoundException;
import com.cricbuzz.news.exception.UserAlreadyExistingException;
import com.cricbuzz.news.repository.UserRepository;
import com.cricbuzz.news.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void test_CreateUser_Success() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("John Doe");
        userRequestDTO.setEmail("john.doe@example.com");
        userRequestDTO.setMobile("+1234567890");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setMobile("+1234567890");
        user.setUserCreatedAt(Instant.now());

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO createdUser = userService.createUser(userRequestDTO);

        assertNotNull(createdUser);
        assertEquals(1L, createdUser.getId());
        assertEquals("John Doe", createdUser.getName());
        assertEquals("john.doe@example.com", createdUser.getEmail());

        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void test_CreateUser_UserAlreadyExists_Throw_UserAlreadyExistingException() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("John Doe");
        userRequestDTO.setEmail("john.doe@example.com");
        userRequestDTO.setMobile("+1234567890");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setMobile("+1234567890");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        UserAlreadyExistingException exception = assertThrows(UserAlreadyExistingException.class, () -> {
            userService.createUser(userRequestDTO);
        });

        assertEquals("User already exists with email : john.doe@example.com", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void test_CreateUser_UnexpectedError_Throw_RuntimeException() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("John Doe");
        userRequestDTO.setEmail("john.doe@example.com");
        userRequestDTO.setMobile("+1234567890");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(userRequestDTO);
        });

        assertEquals("Unexpected error occurred while creating user", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void test_GetUserById_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setMobile("+1234567890");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO userResponseDTO = userService.getUserById(1L);

        assertNotNull(userResponseDTO);
        assertEquals(1L, userResponseDTO.getId());
        assertEquals("John Doe", userResponseDTO.getName());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void test_GetUserById_UserNotFound_Throw_ResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals("User not found with userId : 1", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void test_GetUserById_UnexpectedError_Throw_RuntimeException() {

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setMobile("+1234567890");

        when(userRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals("Unexpected error occurred while fetching user", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void test_GetAllUsers_Success() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setMobile("+1234567890");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setMobile("+0987654321");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserResponseDTO> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("Jane Doe", users.get(1).getName());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void test_GetAllUsers_UnexpectedError_Throw_RuntimeException() {

        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });

        assertEquals("Unexpected error occurred while fetching users", exception.getMessage());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void test_DeleteUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setMobile("+1234567890");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void test_DeleteUser_UserNotFound_Throw_ResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("User not found with userId : 1", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void test_DeleteUser_UnexpectedError_Throw_RuntimeException() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setMobile("+1234567890");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Database error")).when(userRepository).delete(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("Unexpected error occurred while deleting user", exception.getMessage());

        verify(userRepository, times(1)).delete(user);
    }
}