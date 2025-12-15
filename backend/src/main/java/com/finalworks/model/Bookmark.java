package com.finalworks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "final_work_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "final_work_id", nullable = false)
    private FinalWork finalWork;

    @Column(nullable = false)
    private LocalDateTime bookmarkedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        bookmarkedAt = LocalDateTime.now();
    }
}
