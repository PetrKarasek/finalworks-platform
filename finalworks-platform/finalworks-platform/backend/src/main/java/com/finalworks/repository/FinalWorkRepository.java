package com.finalworks.repository;

import com.finalworks.model.FinalWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinalWorkRepository extends JpaRepository<FinalWork, Long> {
    List<FinalWork> findByStudentId(Long studentId);
    List<FinalWork> findAllByOrderBySubmittedAtDesc();
}

