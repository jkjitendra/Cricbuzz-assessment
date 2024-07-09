package com.cricbuzz.news.service.impl;

import com.cricbuzz.news.entity.Tag;
import com.cricbuzz.news.mapper.NewsMapper;
import com.cricbuzz.news.constants.AppConstants;
import com.cricbuzz.news.dto.NewsRequestDTO;
import com.cricbuzz.news.dto.NewsResponseDTO;
import com.cricbuzz.news.dto.PageableResponse;
import com.cricbuzz.news.entity.News;
import com.cricbuzz.news.entity.User;
import com.cricbuzz.news.exception.ResourceNotFoundException;
import com.cricbuzz.news.repository.NewsRepository;
import com.cricbuzz.news.repository.TagRepository;
import com.cricbuzz.news.repository.UserRepository;
import com.cricbuzz.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Override
    public NewsResponseDTO createNews(NewsRequestDTO newsRequestDTO) {
        User author = userRepository.findById(newsRequestDTO.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", newsRequestDTO.getAuthorId()));

        Tag tag = tagRepository.findByName(newsRequestDTO.getTagName())
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "name", newsRequestDTO.getTagName()));

        News news = NewsMapper.toEntity(newsRequestDTO, author, tag);
        News savedNews = newsRepository.save(news);
        return NewsMapper.toResponseDto(savedNews);
    }

    public PageableResponse<NewsResponseDTO> getNewsByTag(String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, AppConstants.SORT_BY));
        Page<News> newsPage = newsRepository.findByTag_Name(tag, pageable);

        List<NewsResponseDTO> content = newsPage.getContent().stream()
                .map(NewsMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PageableResponse<>(content, newsPage.getNumber(), newsPage.getSize(), newsPage.getTotalElements(), newsPage.getTotalPages(), newsPage.isLast());
    }

    @Override
    public PageableResponse<NewsResponseDTO> getAllNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, AppConstants.SORT_BY));
        Page<News> newsPage = newsRepository.findByIsNewsDeletedFalse(pageable);

        List<NewsResponseDTO> content = newsPage.getContent().stream()
                .map(NewsMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PageableResponse<>(content, newsPage.getNumber(), newsPage.getSize(), newsPage.getTotalElements(), newsPage.getTotalPages(), newsPage.isLast());
    }
}
