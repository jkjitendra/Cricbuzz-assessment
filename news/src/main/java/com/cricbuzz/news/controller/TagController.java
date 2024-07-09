package com.cricbuzz.news.controller;

import com.cricbuzz.news.dto.TagDTO;
import com.cricbuzz.news.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping("/")
    public ResponseEntity<TagDTO> addTag(@RequestBody TagDTO tagDTO) {
        TagDTO createdTag = tagService.addTag(tagDTO);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }
}
