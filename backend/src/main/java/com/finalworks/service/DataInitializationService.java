package com.finalworks.service;

import com.finalworks.model.FinalWork;
import com.finalworks.model.Role;
import com.finalworks.model.Student;
import com.finalworks.model.Tag;
import com.finalworks.repository.FinalWorkRepository;
import com.finalworks.repository.StudentRepository;
import com.finalworks.service.TagService;
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

    @Autowired
    private TagService tagService;

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
        // Check if first demo work exists and add tags if missing
        FinalWork existingWork1 = finalWorkRepository.findByTitle("Moderní webové aplikace s React a Spring Boot").orElse(null);
        if (existingWork1 != null && existingWork1.getTags().isEmpty()) {
            existingWork1.getTags().add(tagService.findOrCreateTag("web"));
            existingWork1.getTags().add(tagService.findOrCreateTag("frontend"));
            existingWork1.getTags().add(tagService.findOrCreateTag("frameworks"));
            finalWorkRepository.save(existingWork1);
            logger.info("Added tags to existing work: {}", existingWork1.getTitle());
        } else if (existingWork1 == null) {
            // Create new demo work with tags
            FinalWork work1 = new FinalWork();
            work1.setTitle("Moderní webové aplikace s React a Spring Boot");
            work1.setDescription("Práce se zaměřuje na vývoj moderních webových aplikací pomocí Reactu na frontendu a Spring Boot na backendu. Zahrnuje analýzu best practices, architekturu, a implementaci REST API.");
            work1.setFileUrl("https://example.com/works/react-spring-boot.pdf");
            work1.setStudent(student1);
            work1.setSubmittedAt(LocalDateTime.now().minusDays(5));
            // Add sample tags
            work1.getTags().add(tagService.findOrCreateTag("web"));
            work1.getTags().add(tagService.findOrCreateTag("frontend"));
            work1.getTags().add(tagService.findOrCreateTag("frameworks"));
            finalWorkRepository.save(work1);
            logger.info("Created sample work: {}", work1.getTitle());
        }

        // Check if second demo work exists and add tags if missing
        FinalWork existingWork2 = finalWorkRepository.findByTitle("Implementace bezpečnostních protokolů v distribuovaných systémech").orElse(null);
        if (existingWork2 != null && existingWork2.getTags().isEmpty()) {
            existingWork2.getTags().add(tagService.findOrCreateTag("security"));
            existingWork2.getTags().add(tagService.findOrCreateTag("distributed-systems"));
            existingWork2.getTags().add(tagService.findOrCreateTag("protocols"));
            finalWorkRepository.save(existingWork2);
            logger.info("Added tags to existing work: {}", existingWork2.getTitle());
        } else if (existingWork2 == null) {
            // Create new demo work with tags
            FinalWork work2 = new FinalWork();
            work2.setTitle("Implementace bezpečnostních protokolů v distribuovaných systémech");
            work2.setDescription("Práce se zaměřuje na implementaci a testování bezpečnostních protokolů v distribuovaných systémech. Zahrnuje analýzu TLS/SSL, OAuth 2.0 a dalších moderních bezpečnostních řešení.");
            work2.setFileUrl("https://example.com/works/security-protocols.pdf");
            work2.setStudent(student2);
            work2.setSubmittedAt(LocalDateTime.now().minusDays(3));
            // Add sample tags
            work2.getTags().add(tagService.findOrCreateTag("security"));
            work2.getTags().add(tagService.findOrCreateTag("distributed-systems"));
            work2.getTags().add(tagService.findOrCreateTag("protocols"));
            finalWorkRepository.save(work2);
            logger.info("Created sample work: {}", work2.getTitle());
        }

        // Add third demo work that shares tags with existing works
        FinalWork existingWork3 = finalWorkRepository.findByTitle("React Native mobilní aplikace").orElse(null);
        if (existingWork3 != null && existingWork3.getTags().isEmpty()) {
            existingWork3.getTags().add(tagService.findOrCreateTag("web"));
            existingWork3.getTags().add(tagService.findOrCreateTag("frontend"));
            existingWork3.getTags().add(tagService.findOrCreateTag("mobile"));
            finalWorkRepository.save(existingWork3);
            logger.info("Added tags to existing work: {}", existingWork3.getTitle());
        } else if (existingWork3 == null) {
            // Create new demo work with overlapping tags
            FinalWork work3 = new FinalWork();
            work3.setTitle("React Native mobilní aplikace");
            work3.setDescription("Vývoj cross-platform mobilní aplikace pomocí React Native. Práce pokrývá návrh UI/UX, integraci s backend API, a nasazení na iOS a Android platformy.");
            work3.setFileUrl("https://example.com/works/react-native-app.pdf");
            work3.setStudent(student1);
            work3.setSubmittedAt(LocalDateTime.now().minusDays(1));
            // Add sample tags (shares 'web' and 'frontend' with first work)
            work3.getTags().add(tagService.findOrCreateTag("web"));
            work3.getTags().add(tagService.findOrCreateTag("frontend"));
            work3.getTags().add(tagService.findOrCreateTag("mobile"));
            finalWorkRepository.save(work3);
            logger.info("Created sample work: {}", work3.getTitle());
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
