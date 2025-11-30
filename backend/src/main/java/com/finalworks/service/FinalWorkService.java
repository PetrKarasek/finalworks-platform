package com.finalworks.service;

import com.finalworks.dto.CommentDTO;
import com.finalworks.dto.FinalWorkDTO;
import com.finalworks.exception.BadRequestException;
import com.finalworks.exception.ResourceNotFoundException;
import com.finalworks.model.Comment;
import com.finalworks.model.FinalWork;
import com.finalworks.model.Student;
import com.finalworks.repository.CommentRepository;
import com.finalworks.repository.FinalWorkRepository;
import com.finalworks.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinalWorkService {

    @Autowired
    private FinalWorkRepository finalWorkRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<FinalWorkDTO> getAllFinalWorks() {
        return finalWorkRepository.findAllByOrderBySubmittedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FinalWorkDTO getFinalWorkById(Long id) {
        FinalWork finalWork = finalWorkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + id));
        return convertToDTO(finalWork);
    }

    @Transactional
    public FinalWorkDTO createFinalWork(FinalWorkDTO finalWorkDTO) {
        // Validation is handled by @Valid annotation in controller
        // Additional business logic validation
        Student student = studentRepository.findById(finalWorkDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + finalWorkDTO.getStudentId()));

        FinalWork finalWork = new FinalWork();
        finalWork.setTitle(finalWorkDTO.getTitle().trim());
        finalWork.setDescription(finalWorkDTO.getDescription() != null ? finalWorkDTO.getDescription().trim() : null);
        finalWork.setFileUrl(finalWorkDTO.getFileUrl().trim());
        finalWork.setStudent(student);

        FinalWork saved = finalWorkRepository.save(finalWork);
        // Return the saved entity with all generated fields (id, submittedAt, etc.)
        return convertToDTO(saved);
    }

    @Transactional
    public FinalWorkDTO updateFinalWork(Long id, FinalWorkDTO finalWorkDTO) {
        FinalWork finalWork = finalWorkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + id));

        // Update only provided fields, preserving existing values for others
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
        // Return the updated entity with all current values
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteFinalWork(Long id) {
        if (!finalWorkRepository.existsById(id)) {
            throw new ResourceNotFoundException("Final work not found with id: " + id);
        }
        finalWorkRepository.deleteById(id);
    }

    public List<CommentDTO> getCommentsByFinalWorkId(Long id) {
        return commentRepository.findByFinalWorkIdOrderByCreatedAtAsc(id).stream()
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO addComment(Long finalWorkId, CommentDTO commentDTO) {
        // Validation is handled by @Valid annotation in controller
        FinalWork finalWork = finalWorkRepository.findById(finalWorkId)
                .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + finalWorkId));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent().trim());
        comment.setAuthorName(commentDTO.getAuthorName().trim());
        comment.setFinalWork(finalWork);

        Comment saved = commentRepository.save(comment);
        // Return the saved comment with all generated fields (id, createdAt, etc.)
        return convertCommentToDTO(saved);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
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

