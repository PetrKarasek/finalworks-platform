package com.finalworks.service;

import com.finalworks.dto.RatingDTO;
import com.finalworks.exception.ConflictException;
import com.finalworks.exception.ResourceNotFoundException;
import com.finalworks.model.FinalWork;
import com.finalworks.model.Rating;
import com.finalworks.model.Student;
import com.finalworks.repository.FinalWorkRepository;
import com.finalworks.repository.RatingRepository;
import com.finalworks.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {

    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    private final RatingRepository ratingRepository;
    private final FinalWorkRepository finalWorkRepository;
    private final StudentRepository studentRepository;

    public RatingService(RatingRepository ratingRepository, FinalWorkRepository finalWorkRepository, StudentRepository studentRepository) {
        this.ratingRepository = ratingRepository;
        this.finalWorkRepository = finalWorkRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public RatingDTO rateWork(Long finalWorkId, int rating) {
        logger.info("Rating work {} with {} stars", finalWorkId, rating);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Student student = (Student) authentication.getPrincipal();

            FinalWork finalWork = finalWorkRepository.findById(finalWorkId)
                    .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + finalWorkId));

            if (ratingRepository.existsByStudentAndFinalWork(student, finalWork)) {
                logger.warn("Student {} already rated work {}", student.getId(), finalWorkId);
                throw new ConflictException("You have already rated this work");
            }

            Rating newRating = new Rating();
            newRating.setStudent(student);
            newRating.setFinalWork(finalWork);
            newRating.setRating(rating);

            Rating saved = ratingRepository.save(newRating);
            logger.info("Created rating with id: {}", saved.getId());
            return convertToDTO(saved);
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Optimistic lock exception when rating work {}", finalWorkId, e);
            throw new ResourceNotFoundException("The work was modified by another user. Please refresh and try again.");
        } catch (ResourceNotFoundException | ConflictException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error rating work {}", finalWorkId, e);
            throw e;
        }
    }

    @Transactional
    public void removeRating(Long finalWorkId) {
        logger.info("Removing rating for work {}", finalWorkId);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Student student = (Student) authentication.getPrincipal();

            FinalWork finalWork = finalWorkRepository.findById(finalWorkId)
                    .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + finalWorkId));

            ratingRepository.deleteByStudentAndFinalWork(student, finalWork);
            logger.info("Removed rating for work {}", finalWorkId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error removing rating for work {}", finalWorkId, e);
            throw e;
        }
    }

    public Double getAverageRating(Long finalWorkId) {
        return ratingRepository.getAverageRatingForWork(
                finalWorkRepository.findById(finalWorkId)
                        .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + finalWorkId))
        );
    }

    public Long getRatingCount(Long finalWorkId) {
        return ratingRepository.getRatingCountForWork(
                finalWorkRepository.findById(finalWorkId)
                        .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + finalWorkId))
        );
    }

    private RatingDTO convertToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setStudentId(rating.getStudent().getId());
        dto.setFinalWorkId(rating.getFinalWork().getId());
        dto.setRating(rating.getRating());
        return dto;
    }
}
