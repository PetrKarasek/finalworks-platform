package com.finalworks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalWorkDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "File URL is required")
    private String fileUrl;
    
    private LocalDateTime submittedAt;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    private String studentName;
    private String studentEmail;
    private List<CommentDTO> comments;
}

