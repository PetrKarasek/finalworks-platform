package com.finalworks.repository;

import com.finalworks.model.FatalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FatalLogRepository extends JpaRepository<FatalLog, Long> {
    List<FatalLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<FatalLog> findByLoggerNameContainingIgnoreCase(String loggerName);
}

