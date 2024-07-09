package com.cricbuzz.news.service.impl;

import com.cricbuzz.news.mapper.TagMapper;
import com.cricbuzz.news.dto.TagDTO;
import com.cricbuzz.news.entity.Tag;
import com.cricbuzz.news.repository.TagRepository;
import com.cricbuzz.news.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public TagDTO addTag(TagDTO tagDTO) {
        Tag tag = TagMapper.toEntity(tagDTO);
        Tag savedTag = tagRepository.save(tag);
        return TagMapper.toDTO(savedTag);
    }
}
