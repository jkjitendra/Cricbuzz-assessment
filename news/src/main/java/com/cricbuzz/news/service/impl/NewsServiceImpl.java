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
import com.cricbuzz.news.service.FileStorageService;
import com.cricbuzz.news.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, UserRepository userRepository, TagRepository tagRepository, FileStorageService fileStorageService) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public NewsResponseDTO createNews(NewsRequestDTO newsRequestDTO, MultipartFile image) {
        logger.info("Creating news with title: {}", newsRequestDTO.getTitle());
        try {
            User author = userRepository.findById(newsRequestDTO.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", newsRequestDTO.getAuthorId()));

            Tag tag = tagRepository.findByName(newsRequestDTO.getTagName())
                    .orElseThrow(() -> new ResourceNotFoundException("Tag", "name", newsRequestDTO.getTagName()));

            if (image != null && !image.isEmpty()) {
                String filePath = fileStorageService.storeFile(image);
                newsRequestDTO.setImageUrl(filePath);
            }

            News news = NewsMapper.toEntity(newsRequestDTO, author, tag);
            News savedNews = newsRepository.save(news);
            logger.info("News created successfully with ID: {}", savedNews.getId());
            return NewsMapper.toResponseDto(savedNews);
        } catch (ResourceNotFoundException e) {
            logger.error("Error creating news: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating news: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while creating news", e);
        }
    }

    @Override
    public PageableResponse<NewsResponseDTO> getNewsByTag(String tag, int page, int size) {
        logger.info("Fetching news with tag: {}", tag);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, AppConstants.SORT_BY));
        Page<News> newsPage = newsRepository.findByTag_Name(tag, pageable);

        List<NewsResponseDTO> content = newsPage.getContent().stream()
                .map(NewsMapper::toResponseDto)
                .collect(Collectors.toList());

        logger.info("Fetched {} news articles with tag: {}", newsPage.getTotalElements(), tag);
        return new PageableResponse<>(content, newsPage.getNumber(), newsPage.getSize(), newsPage.getTotalElements(), newsPage.getTotalPages(), newsPage.isLast());
    }

    @Override
    public PageableResponse<NewsResponseDTO> getAllNews(int page, int size) {
        logger.info("Fetching all news with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, AppConstants.SORT_BY));
        Page<News> newsPage = newsRepository.findByIsNewsDeletedFalse(pageable);

        List<NewsResponseDTO> content = newsPage.getContent().stream()
                .map(NewsMapper::toResponseDto)
                .collect(Collectors.toList());

        logger.info("Fetched {} news articles", newsPage.getTotalElements());
        return new PageableResponse<>(content, newsPage.getNumber(), newsPage.getSize(), newsPage.getTotalElements(), newsPage.getTotalPages(), newsPage.isLast());
    }
}
