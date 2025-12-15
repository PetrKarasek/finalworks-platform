package com.finalworks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private Long id;
    private Long studentId;
    private Long finalWorkId;
    private LocalDateTime bookmarkedAt;
}
