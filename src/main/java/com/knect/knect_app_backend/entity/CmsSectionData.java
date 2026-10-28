package com.knect.knect_app_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "section_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmsSectionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_data_id")
    private Integer sectionDataId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore
    private CmsSection section;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "additional_data", columnDefinition = "LONGTEXT")
    private String additionalData;

    @Column(length = 50)
    private String type;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
