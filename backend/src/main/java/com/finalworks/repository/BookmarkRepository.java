package com.finalworks.repository;

import com.finalworks.model.Bookmark;
import com.finalworks.model.FinalWork;
import com.finalworks.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByStudentAndFinalWork(Student student, FinalWork finalWork);
    boolean existsByStudentAndFinalWork(Student student, FinalWork finalWork);
    List<Bookmark> findByStudentOrderByBookmarkedAtDesc(Student student);
    void deleteByStudentAndFinalWork(Student student, FinalWork finalWork);
}
