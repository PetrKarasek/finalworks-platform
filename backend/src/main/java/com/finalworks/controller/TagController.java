package com.finalworks.controller;

import com.finalworks.dto.TagDTO;
import com.finalworks.service.TagService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = {"http://localhost:3000", "https://localhost:3000"})
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        logger.info("Fetching all tags");
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TagDTO>> getPopularTags() {
        logger.info("Fetching popular tags");
        return ResponseEntity.ok(tagService.getPopularTags());
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagDTO tagDTO) {
        logger.info("Creating tag: {}", tagDTO.getName());
        return ResponseEntity.status(201).body(tagService.createTag(tagDTO));
    }
}
