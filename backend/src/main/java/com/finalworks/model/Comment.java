package com.finalworks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT", length = 2000)
    private String content;

    @Column(nullable = false, length = 100)
    private String authorName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version; // Optimistic locking for concurrent access

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "final_work_id", nullable = false)
    private FinalWork finalWork;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

