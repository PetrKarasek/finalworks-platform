package com.finalworks.service;

import com.finalworks.dto.TagDTO;
import com.finalworks.exception.ConflictException;
import com.finalworks.model.Tag;
import com.finalworks.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDTO> getAllTags() {
        logger.debug("Fetching all tags");
        List<TagDTO> tags = tagRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Fetched {} tags", tags.size());
        return tags;
    }

    public List<TagDTO> getPopularTags() {
        logger.debug("Fetching popular tags");
        List<TagDTO> tags = tagRepository.findAllByPopularityDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Fetched {} popular tags", tags.size());
        return tags;
    }

    @Transactional
    public TagDTO createTag(TagDTO tagDTO) {
        logger.info("Creating tag: {}", tagDTO.getName());
        if (tagRepository.existsByName(tagDTO.getName().trim())) {
            logger.warn("Tag already exists: {}", tagDTO.getName());
            throw new ConflictException("Tag already exists: " + tagDTO.getName());
        }

        Tag tag = new Tag();
        tag.setName(tagDTO.getName().trim());
        Tag saved = tagRepository.save(tag);
        logger.info("Created tag with id: {}", saved.getId());
        return convertToDTO(saved);
    }

    Tag findOrCreateTag(String name) {
        String trimmedName = name.trim();
        return tagRepository.findByName(trimmedName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(trimmedName);
                    return tagRepository.save(newTag);
                });
    }

    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }
}
