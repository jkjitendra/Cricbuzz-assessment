package com.cricbuzz.news.service;

import com.cricbuzz.news.dto.NewsRequestDTO;
import com.cricbuzz.news.dto.TagDTO;
import com.cricbuzz.news.entity.News;
import com.cricbuzz.news.entity.Tag;
import com.cricbuzz.news.entity.User;
import com.cricbuzz.news.repository.TagRepository;
import com.cricbuzz.news.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;


    @Test
    void test_AddTag_Success() {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("Test Tag");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");

        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        TagDTO createdTag = tagService.addTag(tagDTO);

        assertNotNull(createdTag);
        assertEquals(1L, createdTag.getId());
        assertEquals("Test Tag", createdTag.getName());

        verify(tagRepository, times(1)).save(any(Tag.class));
    }


    @Test
    void test_AddTag_UnexpectedError_Throw_RuntimeException() {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("Test Tag");

        when(tagRepository.save(any(Tag.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tagService.addTag(tagDTO);
        });

        assertEquals("Unexpected error occurred while adding tag", exception.getMessage());

        verify(tagRepository, times(1)).save(any(Tag.class));
    }

}