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
    
    @NotBlank(message = "Nesmí být prázdné")
    private String content;
    
    @NotBlank(message = "Musí být zadáno jméno autora")
    private String authorName;
    
    private LocalDateTime createdAt;
    private Long finalWorkId;
}

