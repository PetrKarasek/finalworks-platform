package com.finalworks.service;

import com.finalworks.dto.AuthenticationResponse;
import com.finalworks.dto.StudentDTO;
import com.finalworks.dto.StudentRequestDTO;
import com.finalworks.exception.ConflictException;
import com.finalworks.model.Role;
import com.finalworks.model.Student;
import com.finalworks.repository.StudentRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public AuthenticationResponse authenticate(String email, String password) {
        // This will throw an exception if authentication fails
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        
        // If we get here, authentication was successful
        var student = studentRepository.findByEmail(email.trim().toLowerCase())
            .orElseThrow();

        String token = jwtService.generateToken(student);
        String role = student.getRole() != null ? student.getRole().name() : Role.USER.name();
        return new AuthenticationResponse(token, toStudentDTO(student), role);
    }

    @Transactional
    public AuthenticationResponse register(StudentRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();

        if (studentRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already exists: " + email);
        }

        Student student = new Student();
        student.setName(request.getName().trim());
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setRole(Role.USER);

        Student saved = studentRepository.save(student);

        String token = jwtService.generateToken(saved);
        return new AuthenticationResponse(token, toStudentDTO(saved), saved.getRole().name());
    }

    private StudentDTO toStudentDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setRole(student.getRole() != null ? student.getRole().name() : Role.USER.name());
        return dto;
    }
}
