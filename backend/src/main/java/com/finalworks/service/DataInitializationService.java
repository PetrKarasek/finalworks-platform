package com.finalworks.service;

import com.finalworks.model.FinalWork;
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
        // Only initialize if database is empty
        if (finalWorkRepository.count() == 0) {
            logger.info("Initializing sample data...");
            initializeSampleData();
            logger.info("Sample data initialized successfully");
        } else {
            logger.info("Database already contains data, skipping initialization");
        }
    }

    private void initializeSampleData() {
        // Create sample students
        Student student1 = createStudentIfNotExists(
            "Jan Novák",
            "jan.novak@example.com",
            "Password123"
        );

        Student student2 = createStudentIfNotExists(
            "Marie Svobodová",
            "marie.svobodova@example.com",
            "SecurePass456"
        );

        // Create sample final works
        FinalWork work1 = new FinalWork();
        work1.setTitle("Analýza moderních webových technologií");
        work1.setDescription("Tato práce se zabývá analýzou současných webových technologií a frameworků, včetně React, Vue.js a Angular. Zkoumá jejich výhody, nevýhody a vhodnost pro různé typy aplikací.");
        work1.setFileUrl("https://example.com/works/web-technologies-analysis.pdf");
        work1.setStudent(student1);
        work1.setSubmittedAt(LocalDateTime.now().minusDays(5));
        finalWorkRepository.save(work1);

        FinalWork work2 = new FinalWork();
        work2.setTitle("Implementace bezpečnostních protokolů v distribuovaných systémech");
        work2.setDescription("Práce se zaměřuje na implementaci a testování bezpečnostních protokolů v distribuovaných systémech. Zahrnuje analýzu TLS/SSL, OAuth 2.0 a dalších moderních bezpečnostních řešení.");
        work2.setFileUrl("https://example.com/works/security-protocols.pdf");
        work2.setStudent(student2);
        work2.setSubmittedAt(LocalDateTime.now().minusDays(3));
        finalWorkRepository.save(work2);

        logger.info("Created 2 sample final works");
    }

    private Student createStudentIfNotExists(String name, String email, String password) {
        return studentRepository.findByEmail(email)
            .orElseGet(() -> {
                Student student = new Student();
                student.setName(name);
                student.setEmail(email);
                student.setPassword(passwordEncoder.encode(password));
                return studentRepository.save(student);
            });
    }
}
