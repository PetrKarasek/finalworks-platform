package com.finalworks.service;

import com.finalworks.dto.CommentDTO;
import com.finalworks.dto.FinalWorkDTO;
import com.finalworks.dto.TagDTO;
import com.finalworks.exception.ResourceNotFoundException;
import com.finalworks.model.Comment;
import com.finalworks.model.FinalWork;
import com.finalworks.model.Student;
import com.finalworks.model.Tag;
import com.finalworks.repository.CommentRepository;
import com.finalworks.repository.FinalWorkRepository;
import com.finalworks.repository.RatingRepository;
import com.finalworks.repository.StudentRepository;
import com.finalworks.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FinalWorkService {

    private static final Logger logger = LoggerFactory.getLogger(FinalWorkService.class);

    @Autowired
    private FinalWorkRepository finalWorkRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private RatingRepository ratingRepository;

    public List<FinalWorkDTO> getAllFinalWorks() {
        logger.debug("Fetching all final works");
        try {
            List<FinalWorkDTO> works = finalWorkRepository.findAllByOrderBySubmittedAtDesc().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            logger.info("Successfully fetched {} final works", works.size());
            return works;
        } catch (Exception e) {
            logger.error("Error fetching all final works", e);
            throw e;
        }
    }

    public List<FinalWorkDTO> getNewest() {
        logger.debug("Fetching newest final works");
        List<FinalWorkDTO> works = finalWorkRepository.findNewest().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Fetched {} newest works", works.size());
        return works;
    }

    public List<FinalWorkDTO> getTopRated() {
        logger.debug("Fetching top-rated final works");
        List<FinalWorkDTO> works = finalWorkRepository.findTopRated().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Fetched {} top-rated works", works.size());
        return works;
    }

    public List<FinalWorkDTO> searchByQuery(String query) {
        logger.debug("Searching works with query: {}", query);
        List<FinalWorkDTO> works = finalWorkRepository.findByTitleOrDescriptionContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Found {} works matching query: {}", works.size(), query);
        return works;
    }

    public List<FinalWorkDTO> filterByTags(List<String> tagNames) {
        logger.debug("Filtering works by tags: {}", tagNames);
        List<FinalWorkDTO> works = finalWorkRepository.findByTagNames(tagNames).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Found {} works with tags: {}", works.size(), tagNames);
        return works;
    }

    public FinalWorkDTO getFinalWorkById(Long id) {
        logger.debug("Fetching final work with id: {}", id);
        try {
            FinalWork finalWork = finalWorkRepository.findByIdWithTags(id)
                    .orElseThrow(() -> {
                        logger.warn("Final work not found with id: {}", id);
                        return new ResourceNotFoundException("Final work not found with id: " + id);
                    });
            logger.debug("Successfully fetched final work with id: {}", id);
            return convertToDTO(finalWork);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching final work with id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public FinalWorkDTO createFinalWork(FinalWorkDTO finalWorkDTO) {
        logger.info("Creating final work with title: {}", finalWorkDTO.getTitle());
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Student student = (Student) authentication.getPrincipal();

            FinalWork finalWork = new FinalWork();
            finalWork.setTitle(finalWorkDTO.getTitle().trim());
            finalWork.setDescription(finalWorkDTO.getDescription() != null ? finalWorkDTO.getDescription().trim() : null);
            finalWork.setFileUrl(finalWorkDTO.getFileUrl().trim());
            finalWork.setStudent(student);

            // Handle tags if provided
            if (finalWorkDTO.getTags() != null) {
                Set<Tag> tags = finalWorkDTO.getTags().stream()
                        .map(tagDTO -> tagService.findOrCreateTag(tagDTO.getName()))
                        .collect(Collectors.toSet());
                finalWork.setTags(tags);
            }

            FinalWork saved = finalWorkRepository.save(finalWork);
            logger.info("Successfully created final work with id: {}", saved.getId());
            return convertToDTO(saved);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating final work", e);
            throw e;
        }
    }

    @Transactional
    public FinalWorkDTO updateFinalWork(Long id, FinalWorkDTO finalWorkDTO) {
        logger.info("Updating final work with id: {}", id);
        try {
            FinalWork existing = finalWorkRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Final work not found with id: {}", id);
                        return new ResourceNotFoundException("Final work not found with id: " + id);
                    });

            existing.setTitle(finalWorkDTO.getTitle().trim());
            existing.setDescription(finalWorkDTO.getDescription() != null ? finalWorkDTO.getDescription().trim() : null);
            existing.setFileUrl(finalWorkDTO.getFileUrl().trim());

            // Handle tags if provided
            if (finalWorkDTO.getTags() != null) {
                Set<Tag> tags = finalWorkDTO.getTags().stream()
                        .map(tagDTO -> tagService.findOrCreateTag(tagDTO.getName()))
                        .collect(Collectors.toSet());
                existing.setTags(tags);
            }

            FinalWork saved = finalWorkRepository.save(existing);
            logger.info("Successfully updated final work with id: {}", saved.getId());
            return convertToDTO(saved);
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Optimistic lock exception when updating final work with id: {}", id, e);
            throw new ResourceNotFoundException("Final work was modified by another user. Please refresh and try again.");
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating final work with id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public void deleteFinalWork(Long id) {
        logger.info("Deleting final work with id: {}", id);
        try {
            FinalWork finalWork = finalWorkRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Final work not found with id: {}", id);
                        return new ResourceNotFoundException("Final work not found with id: " + id);
                    });

            finalWorkRepository.delete(finalWork);
            logger.info("Successfully deleted final work with id: {}", id);
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Optimistic lock exception when deleting final work with id: {}", id, e);
            throw new ResourceNotFoundException("Final work was modified by another user. Please refresh and try again.");
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting final work with id: {}", id, e);
            throw e;
        }
    }

    public List<CommentDTO> getCommentsByFinalWorkId(Long id) {
        logger.debug("Fetching comments for final work with id: {}", id);
        try {
            List<CommentDTO> comments = commentRepository.findByFinalWorkIdOrderByCreatedAtAsc(id).stream()
                    .map(this::convertCommentToDTO)
                    .collect(Collectors.toList());
            logger.debug("Successfully fetched {} comments for final work with id: {}", comments.size(), id);
            return comments;
        } catch (Exception e) {
            logger.error("Error fetching comments for final work with id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public CommentDTO addComment(Long finalWorkId, CommentDTO commentDTO) {
        logger.info("Adding comment to final work with id: {}", finalWorkId);
        try {
            FinalWork finalWork = finalWorkRepository.findById(finalWorkId)
                    .orElseThrow(() -> {
                        logger.warn("Final work not found with id: {}", finalWorkId);
                        return new ResourceNotFoundException("Final work not found with id: " + finalWorkId);
                    });

            Comment comment = new Comment();
            comment.setContent(commentDTO.getContent().trim());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Student student = (Student) authentication.getPrincipal();
            comment.setAuthorName(student.getName());
            comment.setFinalWork(finalWork);

            Comment saved = commentRepository.save(comment);
            logger.info("Successfully added comment with id: {} to final work with id: {}", saved.getId(), finalWorkId);
            return convertCommentToDTO(saved);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error adding comment to final work with id: {}", finalWorkId, e);
            throw e;
        }
    }

    @Transactional
    public void deleteComment(Long commentId) {
        logger.info("Deleting comment with id: {}", commentId);
        try {
            if (!commentRepository.existsById(commentId)) {
                logger.warn("Comment not found with id: {}", commentId);
                throw new ResourceNotFoundException("Comment not found with id: " + commentId);
            }
            commentRepository.deleteById(commentId);
            logger.info("Successfully deleted comment with id: {}", commentId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting comment with id: {}", commentId, e);
            throw e;
        }
    }

    private FinalWorkDTO convertToDTO(FinalWork finalWork) {
        FinalWorkDTO dto = new FinalWorkDTO();
        dto.setId(finalWork.getId());
        dto.setTitle(finalWork.getTitle());
        dto.setDescription(finalWork.getDescription());
        dto.setFileUrl(finalWork.getFileUrl());
        dto.setSubmittedAt(finalWork.getSubmittedAt());
        dto.setStudentId(finalWork.getStudent().getId());
        dto.setStudentName(finalWork.getStudent().getName());
        dto.setStudentEmail(finalWork.getStudent().getEmail());
        dto.setComments(finalWork.getComments().stream()
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList()));
        dto.setTags(finalWork.getTags().stream()
                .map(this::convertTagToDTO)
                .collect(Collectors.toSet()));
        dto.setAverageRating(ratingRepository.getAverageRatingForWork(finalWork));
        dto.setRatingCount(ratingRepository.getRatingCountForWork(finalWork));
        return dto;
    }

    private TagDTO convertTagToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    private CommentDTO convertCommentToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setFinalWorkId(comment.getFinalWork().getId());
        return dto;
    }
}

