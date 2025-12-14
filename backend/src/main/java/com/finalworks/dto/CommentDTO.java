package com.finalworks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 2000, message = "Comment must be between 1 and 2000 characters")
    private String content;
    
    @NotBlank(message = "Author name is required")
    @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    private String authorName;
    
    private LocalDateTime createdAt;
    private Long finalWorkId;
}

