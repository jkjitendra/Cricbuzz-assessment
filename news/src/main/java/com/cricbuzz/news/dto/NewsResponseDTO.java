package com.cricbuzz.news.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class NewsResponseDTO {
    private Long id;
    private String title;
    private String heading;
    private String description;
    private String imageUrl;
    private String authorName;
    private String tagName;
    private Instant newsCreatedAt;
    private Instant newsLastUpdatedAt;

}
