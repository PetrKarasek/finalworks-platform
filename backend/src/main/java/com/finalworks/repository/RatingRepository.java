package com.finalworks.repository;

import com.finalworks.model.FinalWork;
import com.finalworks.model.Rating;
import com.finalworks.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByStudentAndFinalWork(Student student, FinalWork finalWork);
    boolean existsByStudentAndFinalWork(Student student, FinalWork finalWork);
    void deleteByStudentAndFinalWork(Student student, FinalWork finalWork);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.finalWork = :finalWork")
    Double getAverageRatingForWork(FinalWork finalWork);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.finalWork = :finalWork")
    Long getRatingCountForWork(FinalWork finalWork);
}
