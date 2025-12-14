package com.finalworks.controller;

import com.finalworks.dto.StudentDTO;
import com.finalworks.dto.StudentRequestDTO;
import com.finalworks.exception.BadRequestException;
import com.finalworks.exception.ConflictException;
import com.finalworks.exception.ResourceNotFoundException;
import com.finalworks.model.Student;
import com.finalworks.repository.StudentRepository;
import com.finalworks.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = {"http://localhost:3000", "https://localhost:3000"})
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        logger.debug("Fetching all students");
        try {
            List<StudentDTO> students = studentRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            logger.info("Successfully fetched {} students", students.size());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error fetching all students", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        logger.debug("Fetching student with id: {}", id);
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Student not found with id: {}", id);
                        return new ResourceNotFoundException("Student not found with id: " + id);
                    });
            logger.debug("Successfully fetched student with id: {}", id);
            return ResponseEntity.ok(convertToDTO(student));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching student with id: {}", id, e);
            throw e;
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<StudentDTO> createStudent(@jakarta.validation.Valid @RequestBody StudentRequestDTO studentRequest) {
        logger.info("Creating new student with email: {}", studentRequest.getEmail());
        try {
            String email = studentRequest.getEmail().trim().toLowerCase();
            
            // Zkontrolovat, zda email již existuje
            if (studentRepository.findByEmail(email).isPresent()) {
                logger.warn("Attempt to create student with existing email: {}", email);
                throw new ConflictException("Email already exists: " + email);
            }
            
            Student student = new Student();
            student.setName(studentRequest.getName().trim());
            student.setEmail(email);
            // Hashovat heslo před uložením - heslo se nikdy neukládá jako prostý text
            // BCrypt automaticky generuje salt a hash
            student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
            
            Student saved = studentRepository.save(student);
            logger.info("Successfully created student with id: {} and email: {}", saved.getId(), saved.getEmail());
            
            // Send confirmation email
            emailService.sendRegistrationConfirmation(saved.getEmail(), saved.getName());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(saved));
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating student", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @jakarta.validation.Valid @RequestBody StudentRequestDTO studentRequest) {
        logger.info("Updating student with id: {}", id);
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Student not found with id: {}", id);
                        return new ResourceNotFoundException("Student not found with id: " + id);
                    });
            
            String email = studentRequest.getEmail().trim().toLowerCase();
            
            // Zkontrolovat, zda se email mění a zda nový email již existuje
            if (!student.getEmail().equals(email)) {
                if (studentRepository.findByEmail(email).isPresent()) {
                    logger.warn("Attempt to update student {} with existing email: {}", id, email);
                    throw new ConflictException("Email already exists: " + email);
                }
            }
            
            student.setName(studentRequest.getName().trim());
            student.setEmail(email);
            
            // Hashovat heslo, pokud se aktualizuje - heslo se nikdy neukládá jako prostý text
            if (studentRequest.getPassword() != null && !studentRequest.getPassword().isEmpty()) {
                logger.debug("Updating password for student with id: {}", id);
                student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
            }
            
            Student updated = studentRepository.save(student);
            logger.info("Successfully updated student with id: {}", id);
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (ResourceNotFoundException | ConflictException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating student with id: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        logger.info("Deleting student with id: {}", id);
        try {
            if (!studentRepository.existsById(id)) {
                logger.warn("Student not found with id: {}", id);
                throw new ResourceNotFoundException("Student not found with id: " + id);
            }
            studentRepository.deleteById(id);
            logger.info("Successfully deleted student with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting student with id: {}", id, e);
            throw e;
        }
    }

    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setRole(student.getRole() != null ? student.getRole().name() : null);
        // Heslo je záměrně vyloučeno - nikdy se nevrací
        return dto;
    }
}

