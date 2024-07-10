package com.cricbuzz.news.controller;

import com.cricbuzz.news.dto.APIResponse;
import com.cricbuzz.news.dto.TagDTO;
import com.cricbuzz.news.service.TagService;
import jakarta.validation.Valid;
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
    public ResponseEntity<APIResponse<TagDTO>> addTag(@RequestBody @Valid TagDTO tagDTO) {
        TagDTO createdTag = tagService.addTag(tagDTO);
        APIResponse<TagDTO> response = new APIResponse<>(true, "Tag created successfully", createdTag);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
