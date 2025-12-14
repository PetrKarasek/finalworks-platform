package com.finalworks.dto;

public class AuthenticationResponse {
    private String token;

    private StudentDTO user;

    private String role;
    
    public AuthenticationResponse(String token, StudentDTO user, String role) {
        this.token = token;
        this.user = user;
        this.role = role;
    }
    
    // Getter and setter
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }

    public StudentDTO getUser() {
        return user;
    }

    public void setUser(StudentDTO user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
