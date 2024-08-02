package com.cricbuzz.news.service;

import com.cricbuzz.news.dto.NewsRequestDTO;
import com.cricbuzz.news.dto.NewsResponseDTO;
import com.cricbuzz.news.dto.PageableResponse;
import com.cricbuzz.news.entity.News;
import com.cricbuzz.news.entity.Tag;
import com.cricbuzz.news.entity.User;
import com.cricbuzz.news.exception.ResourceNotFoundException;
import com.cricbuzz.news.mapper.NewsMapper;
import com.cricbuzz.news.repository.NewsRepository;
import com.cricbuzz.news.repository.TagRepository;
import com.cricbuzz.news.repository.UserRepository;
import com.cricbuzz.news.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private NewsServiceImpl newsService;

    private NewsRequestDTO newsRequestDTO;
    private User user;
    private Tag tag;
    private News news;
    @BeforeEach
    void setUp() {
        newsRequestDTO = new NewsRequestDTO();
        newsRequestDTO.setTitle("Test Title");
        newsRequestDTO.setHeading("Test Heading");
        newsRequestDTO.setDescription("Test Description");
        newsRequestDTO.setAuthorId(1L);
        newsRequestDTO.setTagName("Test Tag");

        user = new User();
        user.setId(1L);
        user.setName("Test User");

        tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");

        news = new News();
        news.setId(1L);
        news.setTitle("Test Title");
        news.setHeading("Test Heading");
        news.setDescription("Test Description");
        news.setAuthor(user);
        news.setTag(tag);
    }

    @Test
    void test_CreateNews_WithImage_Success() throws Exception {
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
//        when(image.getOriginalFilename()).thenReturn("image.jpg");
        when(fileStorageService.storeFile(image)).thenReturn("uploads/image.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tagRepository.findByName("Test Tag")).thenReturn(Optional.of(tag));
        when(newsRepository.save(any(News.class))).thenReturn(news);

        NewsResponseDTO responseDTO = newsService.createNews(newsRequestDTO, image);

        assertNotNull(responseDTO);
        assertEquals("Test Title", responseDTO.getTitle());

        verify(fileStorageService, times(1)).storeFile(image);
        verify(newsRepository, times(1)).save(any(News.class));
    }

    @Test
    void test_CreateNews_WithoutImage_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tagRepository.findByName("Test Tag")).thenReturn(Optional.of(tag));
        when(newsRepository.save(any(News.class))).thenReturn(news);

        NewsResponseDTO responseDTO = newsService.createNews(newsRequestDTO, null);

        assertNotNull(responseDTO);
        assertEquals("Test Title", responseDTO.getTitle());

        verify(fileStorageService, times(0)).storeFile(any(MultipartFile.class));
        verify(newsRepository, times(1)).save(any(News.class));
    }

    @Test
    void test_CreateNews_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            newsService.createNews(newsRequestDTO, null);
        });

        assertEquals("User not found with id : 1", exception.getMessage());
        verify(newsRepository, times(0)).save(any(News.class));
    }

    @Test
    void test_CreateNews_TagNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tagRepository.findByName("Test Tag")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            newsService.createNews(newsRequestDTO, null);
        });

        assertEquals("Tag not found with name : Test Tag", exception.getMessage());
        verify(newsRepository, times(0)).save(any(News.class));
    }

    @Test
    void test_CreateNews_UnexpectedError_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tagRepository.findByName("Test Tag")).thenReturn(Optional.of(tag));
        when(newsRepository.save(any(News.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            newsService.createNews(newsRequestDTO, null);
        });

        assertEquals("Unexpected error occurred while creating news", exception.getMessage());
        verify(newsRepository, times(1)).save(any(News.class));
    }

    @Test
    void test_GetNewsByTag_Success() {
        String tagName = "Test Tag";
        int page = 0;
        int size = 10;


        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");

        News news = new News();
        news.setId(1L);
        news.setTitle("Test Title");
        news.setAuthor(user);
        news.setTag(tag);

        Page<News> newsPage = new PageImpl<>(Arrays.asList(news));
        when(newsRepository.findByTag_Name(eq(tagName), any(Pageable.class))).thenReturn(newsPage);

        PageableResponse<NewsResponseDTO> response = newsService.getNewsByTag(tagName, page, size);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Test Title", response.getContent().get(0).getTitle());

        verify(newsRepository, times(1)).findByTag_Name(eq(tagName), any(Pageable.class));
    }

    @Test
    void testGetAllNews() {
        int page = 0;
        int size = 10;

        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");

        News news = new News();
        news.setId(1L);
        news.setTitle("Test Title");
        news.setAuthor(user);
        news.setTag(tag);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Test User2");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("Test Tag2");

        News news2 = new News();
        news2.setId(2L);
        news2.setTitle("Test Title2");
        news2.setAuthor(user2);
        news2.setTag(tag2);

        Page<News> newsPage = new PageImpl<>(Arrays.asList(news, news2));
        when(newsRepository.findByIsNewsDeletedFalse(any(Pageable.class))).thenReturn(newsPage);

        PageableResponse<NewsResponseDTO> response = newsService.getAllNews(page, size);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("Test Title", response.getContent().get(0).getTitle());
        assertEquals("Test Title2", response.getContent().get(1).getTitle());

        verify(newsRepository, times(1)).findByIsNewsDeletedFalse(any(Pageable.class));
    }
}