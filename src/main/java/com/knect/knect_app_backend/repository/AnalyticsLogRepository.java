package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.AnalyticsLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyticsLogRepository extends JpaRepository<AnalyticsLog, Integer> {
    long countByEventType(String eventType);
    List<AnalyticsLog> findTop20ByEventTypeOrderByCreateAtDesc(String eventType);
}
