package com.finalworks.repository;

import com.finalworks.model.FinalWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinalWorkRepository extends JpaRepository<FinalWork, Long> {
    List<FinalWork> findByStudentId(Long studentId);
    List<FinalWork> findAllByOrderBySubmittedAtDesc();
    boolean existsByTitle(String title);
    Optional<FinalWork> findByTitle(String title);

    @Query("SELECT fw FROM FinalWork fw WHERE fw.title ILIKE %:query% OR fw.description ILIKE %:query%")
    List<FinalWork> findByTitleOrDescriptionContainingIgnoreCase(String query);

    @Query("SELECT fw FROM FinalWork fw JOIN fw.tags t WHERE t.name IN :tagNames")
    List<FinalWork> findByTagNames(List<String> tagNames);

    @Query("SELECT fw FROM FinalWork fw ORDER BY SIZE(fw.ratings) DESC, fw.submittedAt DESC")
    List<FinalWork> findTopRated();

    @Query("SELECT fw FROM FinalWork fw ORDER BY fw.submittedAt DESC")
    List<FinalWork> findNewest();

    @Query("SELECT fw FROM FinalWork fw LEFT JOIN FETCH fw.tags WHERE fw.id = :id")
    Optional<FinalWork> findByIdWithTags(@Param("id") Long id);
}

