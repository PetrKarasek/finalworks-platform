package com.finalworks.dto;

public class AuthenticationResponse {
    private String token;
    
    public AuthenticationResponse(String token) {
        this.token = token;
    }
    
    // Getter and setter
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}
