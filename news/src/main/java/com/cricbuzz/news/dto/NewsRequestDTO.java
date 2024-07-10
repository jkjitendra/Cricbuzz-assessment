package com.cricbuzz.news.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsRequestDTO {
    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Heading is required")
    private String heading;

    @NotEmpty(message = "Description is required")
    private String description;

    private String imageUrl;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotEmpty(message = "Tag name is required")
    private String tagName;

}
