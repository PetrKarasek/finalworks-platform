package com.finalworks.controller;

import com.finalworks.dto.AuthenticationRequest;
import com.finalworks.dto.AuthenticationResponse;
import com.finalworks.dto.StudentRequestDTO;
import com.finalworks.service.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        try {
            AuthenticationResponse response = authenticationService.authenticate(request.getEmail(), request.getPassword());
            logger.info("Login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody StudentRequestDTO request) {
        logger.info("Registration attempt for email: {}", request.getEmail());
        try {
            AuthenticationResponse response = authenticationService.register(request);
            logger.info("Registration successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Registration failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }
}
