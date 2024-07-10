package com.cricbuzz.news.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDTO {
    private Long id;

    @NotEmpty(message = "Tag name is required")
    private String name;

}
