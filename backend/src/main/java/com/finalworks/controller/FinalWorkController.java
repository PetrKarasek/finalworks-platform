package com.finalworks.controller;

import com.finalworks.dto.CommentDTO;
import com.finalworks.dto.FinalWorkDTO;
import com.finalworks.service.FinalWorkService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/final-works")
@CrossOrigin(origins = {"http://localhost:3000", "https://localhost:3000"})
public class FinalWorkController {

    private static final Logger logger = LoggerFactory.getLogger(FinalWorkController.class);

    @Autowired
    private FinalWorkService finalWorkService;

    @GetMapping
    public ResponseEntity<List<FinalWorkDTO>> getAllFinalWorks() {
        List<FinalWorkDTO> finalWorks = finalWorkService.getAllFinalWorks();
        return ResponseEntity.ok(finalWorks);
    }

    @GetMapping("/newest")
    public ResponseEntity<List<FinalWorkDTO>> getNewest() {
        return ResponseEntity.ok(finalWorkService.getNewest());
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<FinalWorkDTO>> getTopRated() {
        return ResponseEntity.ok(finalWorkService.getTopRated());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FinalWorkDTO>> search(@RequestParam String q) {
        return ResponseEntity.ok(finalWorkService.searchByQuery(q));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<FinalWorkDTO>> filter(@RequestParam List<String> tags) {
        return ResponseEntity.ok(finalWorkService.filterByTags(tags));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinalWorkDTO> getFinalWorkById(@PathVariable Long id) {
        FinalWorkDTO finalWork = finalWorkService.getFinalWorkById(id);
        return ResponseEntity.ok(finalWork);
    }

    @PostMapping
    public ResponseEntity<FinalWorkDTO> createFinalWork(@Valid @RequestBody FinalWorkDTO finalWorkDTO) {
        FinalWorkDTO created = finalWorkService.createFinalWork(finalWorkDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinalWorkDTO> updateFinalWork(@PathVariable Long id, @Valid @RequestBody FinalWorkDTO finalWorkDTO) {
        FinalWorkDTO updated = finalWorkService.updateFinalWork(id, finalWorkDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinalWork(@PathVariable Long id) {
        finalWorkService.deleteFinalWork(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long id) {
        List<CommentDTO> comments = finalWorkService.getCommentsByFinalWorkId(id);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO) {
        CommentDTO created = finalWorkService.addComment(id, commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        finalWorkService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}

