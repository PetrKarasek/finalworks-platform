package com.finalworks.controller;

import com.finalworks.dto.BookmarkDTO;
import com.finalworks.service.BookmarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = {"http://localhost:3000", "https://localhost:3000"})
public class BookmarkController {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkController.class);

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/{finalWorkId}")
    public ResponseEntity<BookmarkDTO> bookmarkWork(@PathVariable Long finalWorkId) {
        logger.info("User bookmarking work {}", finalWorkId);
        return ResponseEntity.status(201).body(bookmarkService.bookmarkWork(finalWorkId));
    }

    @DeleteMapping("/{finalWorkId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long finalWorkId) {
        logger.info("User removing bookmark for work {}", finalWorkId);
        bookmarkService.removeBookmark(finalWorkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BookmarkDTO>> getUserBookmarks() {
        logger.info("Fetching user bookmarks");
        return ResponseEntity.ok(bookmarkService.getUserBookmarks());
    }
}
