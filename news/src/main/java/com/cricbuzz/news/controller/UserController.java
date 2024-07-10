package com.cricbuzz.news.controller;

import com.cricbuzz.news.dto.APIResponse;
import com.cricbuzz.news.dto.UserRequestDTO;
import com.cricbuzz.news.dto.UserResponseDTO;
import com.cricbuzz.news.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<APIResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequestBody) {
        UserResponseDTO createdUserRequestBody = this.userService.createUser(userRequestBody);
        APIResponse<UserResponseDTO> response = new APIResponse<>(true, "User created successfully", createdUserRequestBody);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<APIResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = this.userService.getAllUsers();
        APIResponse<List<UserResponseDTO>> response = new APIResponse<>(true, "Users fetched successfully", users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<APIResponse<UserResponseDTO>> getUser(@PathVariable Long userId) {
        UserResponseDTO user = this.userService.getUserById(userId);
        APIResponse<UserResponseDTO> response = new APIResponse<>(true, "User fetched successfully", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable("userId") Long uId) {
        this.userService.deleteUser(uId);
        APIResponse<Void> response = new APIResponse<>(true, "User deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

}
