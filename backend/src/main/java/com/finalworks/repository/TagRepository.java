package com.finalworks.repository;

import com.finalworks.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    boolean existsByName(String name);
    
    @Query("SELECT t FROM Tag t JOIN t.finalWorks fw GROUP BY t.id ORDER BY COUNT(fw.id) DESC")
    List<Tag> findAllByPopularityDesc();
    
    @Query("SELECT t FROM Tag t WHERE SIZE(t.finalWorks) > 0")
    List<Tag> findAllUsed();
}
