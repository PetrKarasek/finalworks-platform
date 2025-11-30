package com.finalworks.repository;

import com.finalworks.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    List<ErrorLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<ErrorLog> findByLoggerNameContainingIgnoreCase(String loggerName);
}

