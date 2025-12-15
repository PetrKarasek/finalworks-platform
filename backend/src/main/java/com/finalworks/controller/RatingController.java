package com.finalworks.controller;

import com.finalworks.dto.RatingDTO;
import com.finalworks.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = {"http://localhost:3000", "https://localhost:3000"})
public class RatingController {

    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/{finalWorkId}")
    public ResponseEntity<RatingDTO> rateWork(@PathVariable Long finalWorkId, @RequestBody RatingDTO ratingDTO) {
        logger.info("User rating work {} with {} stars", finalWorkId, ratingDTO.getRating());
        return ResponseEntity.status(201).body(ratingService.rateWork(finalWorkId, ratingDTO.getRating()));
    }

    @DeleteMapping("/{finalWorkId}")
    public ResponseEntity<Void> removeRating(@PathVariable Long finalWorkId) {
        logger.info("User removing rating for work {}", finalWorkId);
        ratingService.removeRating(finalWorkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{finalWorkId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long finalWorkId) {
        Double avg = ratingService.getAverageRating(finalWorkId);
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }

    @GetMapping("/{finalWorkId}/count")
    public ResponseEntity<Long> getRatingCount(@PathVariable Long finalWorkId) {
        return ResponseEntity.ok(ratingService.getRatingCount(finalWorkId));
    }
}
