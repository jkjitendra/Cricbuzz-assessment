package com.cricbuzz.news.service;

import com.cricbuzz.news.dto.NewsRequestDTO;
import com.cricbuzz.news.dto.NewsResponseDTO;
import com.cricbuzz.news.dto.PageableResponse;

public interface NewsService {

    NewsResponseDTO createNews(NewsRequestDTO news);

    PageableResponse<NewsResponseDTO> getNewsByTag(String tag, int page, int size);

    PageableResponse<NewsResponseDTO> getAllNews(int page, int size);
}
