package com.knect.knect_app_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // VISIT, SEARCH, BUSINESS_VIEW, WHATSAPP_CLICK

    @Column(name = "business_id")
    private Integer businessId;

    @Column(name = "establishment_id")
    private Integer establishmentId;

    @Column(name = "search_query", length = 250)
    private String searchQuery;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}
