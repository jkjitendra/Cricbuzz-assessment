package com.cricbuzz.news.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String mobile;
    private Instant userCreatedAt;
}
