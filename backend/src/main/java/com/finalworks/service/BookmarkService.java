package com.finalworks.service;

import com.finalworks.dto.BookmarkDTO;
import com.finalworks.exception.ConflictException;
import com.finalworks.exception.ResourceNotFoundException;
import com.finalworks.model.Bookmark;
import com.finalworks.model.FinalWork;
import com.finalworks.model.Student;
import com.finalworks.repository.BookmarkRepository;
import com.finalworks.repository.FinalWorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkService.class);

    private final BookmarkRepository bookmarkRepository;
    private final FinalWorkRepository finalWorkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository, FinalWorkRepository finalWorkRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.finalWorkRepository = finalWorkRepository;
    }

    @Transactional
    public BookmarkDTO bookmarkWork(Long finalWorkId) {
        logger.info("Bookmarking work {}", finalWorkId);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Student student = (Student) authentication.getPrincipal();

            FinalWork finalWork = finalWorkRepository.findById(finalWorkId)
                    .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + finalWorkId));

            if (bookmarkRepository.existsByStudentAndFinalWork(student, finalWork)) {
                logger.warn("Student {} already bookmarked work {}", student.getId(), finalWorkId);
                throw new ConflictException("You have already bookmarked this work");
            }

            Bookmark bookmark = new Bookmark();
            bookmark.setStudent(student);
            bookmark.setFinalWork(finalWork);

            Bookmark saved = bookmarkRepository.save(bookmark);
            logger.info("Created bookmark with id: {}", saved.getId());
            return convertToDTO(saved);
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Optimistic lock exception when bookmarking work {}", finalWorkId, e);
            throw new ResourceNotFoundException("The work was modified by another user. Please refresh and try again.");
        } catch (ResourceNotFoundException | ConflictException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error bookmarking work {}", finalWorkId, e);
            throw e;
        }
    }

    @Transactional
    public void removeBookmark(Long finalWorkId) {
        logger.info("Removing bookmark for work {}", finalWorkId);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Student student = (Student) authentication.getPrincipal();

            FinalWork finalWork = finalWorkRepository.findById(finalWorkId)
                    .orElseThrow(() -> new ResourceNotFoundException("Final work not found with id: " + finalWorkId));

            bookmarkRepository.deleteByStudentAndFinalWork(student, finalWork);
            logger.info("Removed bookmark for work {}", finalWorkId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error removing bookmark for work {}", finalWorkId, e);
            throw e;
        }
    }

    public List<BookmarkDTO> getUserBookmarks() {
        logger.debug("Fetching user bookmarks");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        List<BookmarkDTO> bookmarks = bookmarkRepository.findByStudentOrderByBookmarkedAtDesc(student).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Fetched {} bookmarks for user {}", bookmarks.size(), student.getId());
        return bookmarks;
    }

    private BookmarkDTO convertToDTO(Bookmark bookmark) {
        BookmarkDTO dto = new BookmarkDTO();
        dto.setId(bookmark.getId());
        dto.setStudentId(bookmark.getStudent().getId());
        dto.setFinalWorkId(bookmark.getFinalWork().getId());
        dto.setBookmarkedAt(bookmark.getBookmarkedAt());
        return dto;
    }
}
