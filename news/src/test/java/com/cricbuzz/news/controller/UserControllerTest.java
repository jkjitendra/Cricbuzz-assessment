package com.cricbuzz.news.controller;

import com.cricbuzz.news.dto.APIResponse;
import com.cricbuzz.news.dto.UserRequestDTO;
import com.cricbuzz.news.dto.UserResponseDTO;
import com.cricbuzz.news.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUser_Success() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("John Doe");
        userRequestDTO.setEmail("john.doe@example.com");
        userRequestDTO.setMobile("+1234567890");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("John Doe");
        userResponseDTO.setEmail("john.doe@example.com");
        userResponseDTO.setMobile("+1234567890");

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/v1/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.name").value("John Doe"));
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        List<UserResponseDTO> users = new ArrayList<>();
        UserResponseDTO user1 = new UserResponseDTO();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setMobile("+1234567890");

        UserResponseDTO user2 = new UserResponseDTO();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setMobile("+0987654321");

        users.add(user1);
        users.add(user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Users fetched successfully"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                .andExpect(jsonPath("$.data[1].name").value("Jane Doe"));
    }

    @Test
    void testGetUser_Success() throws Exception {
        Long userId = 1L;
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userId);
        userResponseDTO.setName("John Doe");
        userResponseDTO.setEmail("john.doe@example.com");
        userResponseDTO.setMobile("+1234567890");

        when(userService.getUserById(anyLong())).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User fetched successfully"))
                .andExpect(jsonPath("$.data.name").value("John Doe"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }
}