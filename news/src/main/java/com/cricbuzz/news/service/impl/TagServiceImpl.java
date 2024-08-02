package com.cricbuzz.news.service.impl;

import com.cricbuzz.news.mapper.TagMapper;
import com.cricbuzz.news.dto.TagDTO;
import com.cricbuzz.news.entity.Tag;
import com.cricbuzz.news.repository.TagRepository;
import com.cricbuzz.news.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TagServiceImpl implements TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagDTO addTag(TagDTO tagDTO) {
        logger.info("Adding tag with name: {}", tagDTO.getName());
        try {
            Tag tag = TagMapper.toEntity(tagDTO);
            Tag savedTag = tagRepository.save(tag);
            logger.info("Tag added successfully with ID: {}", savedTag.getId());
            return TagMapper.toDTO(savedTag);
        } catch (Exception e) {
            logger.error("Error adding tag: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while adding tag", e);
        }
    }
}