package com.cricbuzz.news.controller;

import com.cricbuzz.news.dto.APIResponse;
import com.cricbuzz.news.dto.TagDTO;
import com.cricbuzz.news.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
    }

    @Test
    void testAddTag_Success() throws Exception {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("Sample Tag");

        TagDTO createdTagDTO = new TagDTO();
        createdTagDTO.setId(1L);
        createdTagDTO.setName("Sample Tag");

        when(tagService.addTag(any(TagDTO.class))).thenReturn(createdTagDTO);

        mockMvc.perform(post("/api/v1/tags/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tag created successfully"))
                .andExpect(jsonPath("$.data.name").value("Sample Tag"));
    }
}