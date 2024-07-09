package com.cricbuzz.news.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsRequestDTO {
    private String title;
    private String heading;
    private String description;
    private String imageUrl;
    private Long authorId;
    private String tagName;

}
