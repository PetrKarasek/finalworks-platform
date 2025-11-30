package com.finalworks.controller;

import com.finalworks.model.FatalLog;
import com.finalworks.repository.FatalLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/fatal-logs")
@CrossOrigin(origins = "https://localhost:3000")
public class FatalLogController {

    @Autowired
    private FatalLogRepository fatalLogRepository;

    @GetMapping
    public ResponseEntity<List<FatalLog>> getAllFatalLogs() {
        List<FatalLog> logs = fatalLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FatalLog> getFatalLogById(@PathVariable Long id) {
        return fatalLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<FatalLog>> getRecentFatalLogs(
            @RequestParam(defaultValue = "24") int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<FatalLog> logs = fatalLogRepository.findByTimestampBetween(since, LocalDateTime.now());
        return ResponseEntity.ok(logs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFatalLog(@PathVariable Long id) {
        if (fatalLogRepository.existsById(id)) {
            fatalLogRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

