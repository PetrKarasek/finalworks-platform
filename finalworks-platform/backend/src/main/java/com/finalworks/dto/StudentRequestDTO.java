package com.finalworks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {
    private String name;
    private String email;
    private String password; // Only used for input, never returned
}

