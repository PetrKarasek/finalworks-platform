package com.finalworks.service;

import com.finalworks.dto.CommentDTO;
import com.finalworks.dto.FinalWorkDTO;
import com.finalworks.exception.ResourceNotFoundException;
import com.finalworks.model.Comment;
import com.finalworks.model.FinalWork;
import com.finalworks.model.Student;
import com.finalworks.repository.CommentRepository;
import com.finalworks.repository.FinalWorkRepository;
import com.finalworks.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public FinalWorkDTO getFinalWorkById(Long id) {
        logger.debug("Fetching final work with id: {}", id);
        try {
            FinalWork finalWork = finalWorkRepository.findById(id)
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
            Student student = studentRepository.findById(finalWorkDTO.getStudentId())
                    .orElseThrow(() -> {
                        logger.warn("Student not found with id: {}", finalWorkDTO.getStudentId());
                        return new ResourceNotFoundException("Student not found with id: " + finalWorkDTO.getStudentId());
                    });

            FinalWork finalWork = new FinalWork();
            finalWork.setTitle(finalWorkDTO.getTitle().trim());
            finalWork.setDescription(finalWorkDTO.getDescription() != null ? finalWorkDTO.getDescription().trim() : null);
            finalWork.setFileUrl(finalWorkDTO.getFileUrl().trim());
            finalWork.setStudent(student);

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
            FinalWork finalWork = finalWorkRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Final work not found with id: {}", id);
                        return new ResourceNotFoundException("Final work not found with id: " + id);
                    });

            // Aktualizovat pouze poskytnutá pole, zachovat existující hodnoty pro ostatní
            if (finalWorkDTO.getTitle() != null && !finalWorkDTO.getTitle().trim().isEmpty()) {
                finalWork.setTitle(finalWorkDTO.getTitle().trim());
            }
            if (finalWorkDTO.getDescription() != null) {
                finalWork.setDescription(finalWorkDTO.getDescription().trim());
            }
            if (finalWorkDTO.getFileUrl() != null && !finalWorkDTO.getFileUrl().trim().isEmpty()) {
                finalWork.setFileUrl(finalWorkDTO.getFileUrl().trim());
            }

            FinalWork updated = finalWorkRepository.save(finalWork);
            logger.info("Successfully updated final work with id: {}", id);
            return convertToDTO(updated);
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
            if (!finalWorkRepository.existsById(id)) {
                logger.warn("Final work not found with id: {}", id);
                throw new ResourceNotFoundException("Final work not found with id: " + id);
            }
            finalWorkRepository.deleteById(id);
            logger.info("Successfully deleted final work with id: {}", id);
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
            comment.setAuthorName(commentDTO.getAuthorName().trim());
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
        return dto;
    }

    private CommentDTO convertCommentToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorName(comment.getAuthorName());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setFinalWorkId(comment.getFinalWork().getId());
        return dto;
    }
}

