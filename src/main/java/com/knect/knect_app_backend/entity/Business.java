package com.knect.knect_app_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "businesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id")
    private Integer businessId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "establishment_type_id")
    private EstablishmentType establishmentType;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "legal_name", length = 180)
    private String legalName;

    @Column(name = "tax_number", length = 50)
    private String taxNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "primary_color", length = 20)
    @Builder.Default
    private String primaryColor = "#00A3FF";

    @Column(name = "logo_url", columnDefinition = "LONGTEXT")
    private String logoUrl;

    @Column(name = "banner_url", columnDefinition = "LONGTEXT")
    private String bannerUrl;

    @Column(length = 120)
    private String neighborhood;

    @Column(length = 250)
    private String address;

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

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "rating_avg")
    @Builder.Default
    private Double ratingAvg = 0.0;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "search_appearance_count")
    @Builder.Default
    private Integer searchAppearanceCount = 0;

    @Column(name = "whatsapp_click_count")
    @Builder.Default
    private Integer whatsappClickCount = 0;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Establishment> establishments = new ArrayList<>();

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        if (primaryColor == null || primaryColor.isBlank()) primaryColor = "#00A3FF";
        if (isGhostKitchen == null) isGhostKitchen = false;
        if (hasDineIn == null) hasDineIn = true;
        if (isFeatured == null) isFeatured = false;
        if (isActive == null) isActive = true;
        if (ratingAvg == null) ratingAvg = 0.0;
        if (reviewCount == null) reviewCount = 0;
        if (viewCount == null) viewCount = 0;
        if (searchAppearanceCount == null) searchAppearanceCount = 0;
        if (whatsappClickCount == null) whatsappClickCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
