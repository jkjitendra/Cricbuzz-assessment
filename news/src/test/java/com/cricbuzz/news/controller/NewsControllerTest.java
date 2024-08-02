package com.cricbuzz.news.controller;

import com.cricbuzz.news.dto.APIResponse;
import com.cricbuzz.news.dto.NewsRequestDTO;
import com.cricbuzz.news.dto.NewsResponseDTO;
import com.cricbuzz.news.dto.PageableResponse;
import com.cricbuzz.news.entity.User;
import com.cricbuzz.news.exception.ResourceNotFoundException;
import com.cricbuzz.news.repository.UserRepository;
import com.cricbuzz.news.service.NewsService;
import com.cricbuzz.news.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsController.class)
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private NewsController newsController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // Ensure UTF-8 encoding
                .build();
    }

    @Test
    void test_CreateNews_Success() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");

        NewsRequestDTO newsRequestDTO = new NewsRequestDTO();
        newsRequestDTO.setTitle("Sample Title");
        newsRequestDTO.setHeading("Sample Heading");
        newsRequestDTO.setDescription("Sample Description");
        newsRequestDTO.setAuthorId(userId);
        newsRequestDTO.setTagName("Sample Tag");

        NewsResponseDTO newsResponseDTO = new NewsResponseDTO();
        newsResponseDTO.setId(1L);
        newsResponseDTO.setTitle("Sample Title");

        MockMultipartFile newsJson = new MockMultipartFile("news", "", "application/json", objectMapper.writeValueAsString(newsRequestDTO).getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "Image Content".getBytes());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(newsService.createNews(any(NewsRequestDTO.class), any(MockMultipartFile.class))).thenReturn(newsResponseDTO);

        mockMvc.perform(multipart("/api/v1/news/user/{userId}/news", userId)
                        .file(newsJson)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("News created successfully"))
                .andExpect(jsonPath("$.data.title").value("Sample Title"));
    }

    @Test
    void test_GetNewsByTag_Success() throws Exception {
        PageableResponse<NewsResponseDTO> pageableResponse = new PageableResponse<>();
        pageableResponse.setPageNumber(0);
        pageableResponse.setSize(10);
        pageableResponse.setTotalElements(1);
        pageableResponse.setTotalPages(1);
        pageableResponse.setLastPage(true);

        when(newsService.getNewsByTag(eq("Sample Tag"), eq(0), eq(10))).thenReturn(pageableResponse);

        mockMvc.perform(get("/api/v1/news/tag")
                        .param("tag", "Sample Tag")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("News fetched successfully"))
                .andExpect(jsonPath("$.data.pageNumber").value(0));
    }

    @Test
    void test_GetAllNews_Success() throws Exception {
        PageableResponse<NewsResponseDTO> pageableResponse = new PageableResponse<>();
        pageableResponse.setPageNumber(0);
        pageableResponse.setSize(10);
        pageableResponse.setTotalElements(1);
        pageableResponse.setTotalPages(1);
        pageableResponse.setLastPage(true);

        when(newsService.getAllNews(eq(0), eq(10))).thenReturn(pageableResponse);

        mockMvc.perform(get("/api/v1/news/")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("News fetched successfully"))
                .andExpect(jsonPath("$.data.pageNumber").value(0));
    }

    @Test
    public void test_CreateNews_ResourceNotFound() throws Exception {
        String newsJson = new ObjectMapper().writeValueAsString(new NewsRequestDTO());
        MockMultipartFile newsPart = new MockMultipartFile("news", "", "application/json", newsJson.getBytes());
        MockMultipartFile imagePart = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);

        doThrow(new ResourceNotFoundException("User", "userId", 1L)).when(newsService).createNews(any(NewsRequestDTO.class), any(MockMultipartFile.class));

        mockMvc.perform(multipart("/api/v1/news/user/{userId}/news", 1L)
                        .file(newsPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetNewsByTag_EmptyResponse() throws Exception {
        when(newsService.getNewsByTag(anyString(), any(Integer.class), any(Integer.class)))
                .thenReturn(new PageableResponse<>(Collections.emptyList(), 0, 10, 0, 1, true));

        mockMvc.perform(get("/api/v1/news/tag")
                        .param("tag", "cricket")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    APIResponse<PageableResponse<NewsResponseDTO>> response = objectMapper.readValue(jsonResponse, new TypeReference<APIResponse<PageableResponse<NewsResponseDTO>>>() {});
                    assertEquals(0, response.getData().getTotalElements());
                });
    }

}