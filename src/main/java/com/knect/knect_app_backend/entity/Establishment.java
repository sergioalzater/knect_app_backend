package com.knect.knect_app_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "establishments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "establishment_id")
    private Integer establishmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    @JsonIgnore
    private Business business;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "establishment_type_id")
    private EstablishmentType establishmentType;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String schedules;

    @Column(length = 120)
    private String neighborhood;

    @Column(length = 250)
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String map;

    @Column(length = 40)
    private String phone;

    @Column(length = 40)
    private String whatsapp;

    @Column(length = 150)
    private String email;

    @Column(length = 255)
    private String website;

    @Column(name = "is_ghost_kitchen")
    @Builder.Default
    private Boolean isGhostKitchen = false;

    @Column(name = "has_dine_in")
    @Builder.Default
    private Boolean hasDineIn = true;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "whatsapp_clicks")
    @Builder.Default
    private Integer whatsappClicks = 0;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        if (isGhostKitchen == null) isGhostKitchen = false;
        if (hasDineIn == null) hasDineIn = true;
        if (isActive == null) isActive = true;
        if (whatsappClicks == null) whatsappClicks = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
