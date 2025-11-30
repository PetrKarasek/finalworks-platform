package com.finalworks.controller;

import com.finalworks.dto.StudentDTO;
import com.finalworks.dto.StudentRequestDTO;
import com.finalworks.exception.BadRequestException;
import com.finalworks.exception.ConflictException;
import com.finalworks.exception.ResourceNotFoundException;
import com.finalworks.model.Student;
import com.finalworks.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "https://localhost:3000")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return ResponseEntity.ok(convertToDTO(student));
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentRequestDTO studentRequest) {
        // Ověřit vstup
        if (studentRequest.getName() == null || studentRequest.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        if (studentRequest.getEmail() == null || studentRequest.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (studentRequest.getPassword() == null || studentRequest.getPassword().isEmpty()) {
            throw new BadRequestException("Password is required");
        }
        
        // Zkontrolovat, zda email již existuje
        if (studentRepository.findByEmail(studentRequest.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + studentRequest.getEmail());
        }
        
        Student student = new Student();
        student.setName(studentRequest.getName());
        student.setEmail(studentRequest.getEmail());
        // Hashovat heslo před uložením - heslo se nikdy neukládá jako prostý text
        student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        
        Student saved = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody StudentRequestDTO studentRequest) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        // Ověřit vstup
        if (studentRequest.getName() == null || studentRequest.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        if (studentRequest.getEmail() == null || studentRequest.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        
        // Zkontrolovat, zda se email mění a zda nový email již existuje
        if (!student.getEmail().equals(studentRequest.getEmail())) {
            if (studentRepository.findByEmail(studentRequest.getEmail()).isPresent()) {
                throw new ConflictException("Email already exists: " + studentRequest.getEmail());
            }
        }
        
        student.setName(studentRequest.getName());
        student.setEmail(studentRequest.getEmail());
        // Hashovat heslo, pokud se aktualizuje - heslo se nikdy neukládá jako prostý text
        if (studentRequest.getPassword() != null && !studentRequest.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        }
        
        Student updated = studentRepository.save(student);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        // Heslo je záměrně vyloučeno - nikdy se nevrací
        return dto;
    }
}

