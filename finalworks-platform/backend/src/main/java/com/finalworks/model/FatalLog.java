package com.finalworks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fatal_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FatalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    @Column(length = 200)
    private String loggerName;

    @Column(length = 50)
    private String threadName;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 100)
    private String className;

    @Column(length = 100)
    private String methodName;

    @Column(length = 50)
    private String level;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

