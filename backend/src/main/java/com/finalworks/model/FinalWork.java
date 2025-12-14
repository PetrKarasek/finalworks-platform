package com.finalworks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "final_works")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", length = 5000)
    private String description;

    @Column(nullable = false, length = 500)
    private String fileUrl;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Version
    private Long version; // Optimistic locking for concurrent access

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @OneToMany(mappedBy = "finalWork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}

