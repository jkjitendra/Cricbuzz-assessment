package com.cricbuzz.news.controller;

import com.cricbuzz.news.constants.AppConstants;
import com.cricbuzz.news.dto.APIResponse;
import com.cricbuzz.news.dto.NewsRequestDTO;
import com.cricbuzz.news.dto.NewsResponseDTO;
import com.cricbuzz.news.dto.PageableResponse;
import com.cricbuzz.news.exception.ResourceNotFoundException;
import com.cricbuzz.news.repository.UserRepository;
import com.cricbuzz.news.service.FileStorageService;
import com.cricbuzz.news.service.NewsService;
import com.cricbuzz.news.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/user/{userId}/news", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<NewsResponseDTO>> createNews(
            @PathVariable Long userId,
            @RequestPart("news") String newsJson,
            @RequestPart("image") MultipartFile image
    ) throws IOException{
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("News", "authorId", userId));
        NewsRequestDTO newsRequestDTO = new ObjectMapper().readValue(newsJson, NewsRequestDTO.class);
        if (image != null && !image.isEmpty()) {
            String filePath = fileStorageService.storeFile(image);
            newsRequestDTO.setImageUrl(filePath);
        }
        NewsResponseDTO createdNewsResponseBody = newsService.createNews(newsRequestDTO);
        APIResponse<NewsResponseDTO> response = new APIResponse<>(true, "News created successfully", createdNewsResponseBody);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/tag")
    public ResponseEntity<APIResponse<PageableResponse<NewsResponseDTO>>> getNewsByTag(
            @RequestParam String tag,
            @RequestParam int page,
            @RequestParam int size
    ) {
        PageableResponse<NewsResponseDTO> pageableResponse = newsService.getNewsByTag(tag, page, size);
        APIResponse<PageableResponse<NewsResponseDTO>> response = new APIResponse<>(true, "News fetched successfully", pageableResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<APIResponse<PageableResponse<NewsResponseDTO>>> getAllNews(
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
    ) {
        PageableResponse<NewsResponseDTO> pageableResponse = newsService.getAllNews(pageNumber, pageSize);
        APIResponse<PageableResponse<NewsResponseDTO>> response = new APIResponse<>(true, "News fetched successfully", pageableResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}