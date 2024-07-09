package com.cricbuzz.news.mapper;

import com.cricbuzz.news.dto.NewsRequestDTO;
import com.cricbuzz.news.dto.NewsResponseDTO;
import com.cricbuzz.news.entity.News;
import com.cricbuzz.news.entity.Tag;
import com.cricbuzz.news.entity.User;

import java.time.Instant;

public class NewsMapper {

    public static NewsResponseDTO toResponseDto(News news) {
        NewsResponseDTO dto = new NewsResponseDTO();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setHeading(news.getHeading());
        dto.setDescription(news.getDescription());
        dto.setImageUrl(news.getImageUrl());
        dto.setAuthorName(news.getAuthor().getName());
        dto.setTagName(news.getTag().getName());
        dto.setNewsCreatedAt(news.getNewsCreatedAt());
        dto.setNewsLastUpdatedAt(news.getNewsLastUpdatedAt());
        return dto;
    }

    public static News toEntity(NewsRequestDTO dto, User author, Tag tag) {
        News news = new News();
        news.setTitle(dto.getTitle());
        news.setHeading(dto.getHeading());
        news.setDescription(dto.getDescription());
        news.setImageUrl(dto.getImageUrl());
        news.setAuthor(author);
        news.setTag(tag);
        news.setNewsCreatedAt(Instant.now());
        news.setNewsLastUpdatedAt(Instant.now());
        return news;
    }
}
