package com.finalworks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    
    @NotBlank(message = "Comment content is required")
    private String content;
    
    @NotBlank(message = "Author name is required")
    private String authorName;
    
    private LocalDateTime createdAt;
    private Long finalWorkId;
}

