package com.finalworks.service;

import com.finalworks.model.FinalWork;
import com.finalworks.model.Role;
import com.finalworks.model.Student;
import com.finalworks.repository.FinalWorkRepository;
import com.finalworks.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FinalWorkRepository finalWorkRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Always ensure admin account exists
        createStudentIfNotExists(
            "Admin",
            "admin@example.com",
            "AdminPass123",
            Role.ADMIN
        );

        // Always ensure sample students exist
        Student student1 = createStudentIfNotExists(
            "Jan Novák",
            "jan.novak@example.com",
            "Password123",
            Role.USER
        );

        Student student2 = createStudentIfNotExists(
            "Marie Svobodová",
            "marie.svobodova@example.com",
            "SecurePass456",
            Role.USER
        );

        // Ensure at least 2 sample final works exist (idempotent)
        ensureSampleWorksExist(student1, student2);
    }

    private void ensureSampleWorksExist(Student student1, Student student2) {
        // Create sample works only if they don't already exist (by title)
        if (!finalWorkRepository.existsByTitle("Analýza moderních webových technologií")) {
            FinalWork work1 = new FinalWork();
            work1.setTitle("Analýza moderních webových technologií");
            work1.setDescription("Tato práce se zabývá analýzou současných webových technologií a frameworků, včetně React, Vue.js a Angular. Zkoumá jejich výhody, nevýhody a vhodnost pro různé typy aplikací.");
            work1.setFileUrl("https://example.com/works/web-technologies-analysis.pdf");
            work1.setStudent(student1);
            work1.setSubmittedAt(LocalDateTime.now().minusDays(5));
            finalWorkRepository.save(work1);
            logger.info("Created sample work: {}", work1.getTitle());
        }

        if (!finalWorkRepository.existsByTitle("Implementace bezpečnostních protokolů v distribuovaných systémech")) {
            FinalWork work2 = new FinalWork();
            work2.setTitle("Implementace bezpečnostních protokolů v distribuovaných systémech");
            work2.setDescription("Práce se zaměřuje na implementaci a testování bezpečnostních protokolů v distribuovaných systémech. Zahrnuje analýzu TLS/SSL, OAuth 2.0 a dalších moderních bezpečnostních řešení.");
            work2.setFileUrl("https://example.com/works/security-protocols.pdf");
            work2.setStudent(student2);
            work2.setSubmittedAt(LocalDateTime.now().minusDays(3));
            finalWorkRepository.save(work2);
            logger.info("Created sample work: {}", work2.getTitle());
        }
    }

    private Student createStudentIfNotExists(String name, String email, String password, Role role) {
        return studentRepository.findByEmail(email)
            .orElseGet(() -> {
                Student student = new Student();
                student.setName(name);
                student.setEmail(email);
                student.setPassword(passwordEncoder.encode(password));
                student.setRole(role);
                return studentRepository.save(student);
            });
    }
}
