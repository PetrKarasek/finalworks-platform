package com.finalworks.service;

import com.finalworks.model.Student;
import com.finalworks.repository.StudentRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(StudentRepository studentRepository, 
                               PasswordEncoder passwordEncoder, 
                               JwtService jwtService,
                               AuthenticationManager authenticationManager) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public String authenticate(String email, String password) {
        // This will throw an exception if authentication fails
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        
        // If we get here, authentication was successful
        var student = studentRepository.findByEmail(email)
            .orElseThrow();
            
        return jwtService.generateToken(student);
    }
}
