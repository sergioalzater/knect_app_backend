package com.knect.knect_app_backend.dto;

import com.knect.knect_app_backend.entity.Business;
import com.knect.knect_app_backend.entity.Establishment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class BusinessDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstablishmentDto {
        private Integer id;
        private Integer establishmentId;
        private Integer businessId;
        private Integer establishmentTypeId;
        private String categoryName;
        private String name;
        private String description;
        private String schedules;
        private String neighborhood;
        private String address;
        private Double latitude;
        private Double longitude;
        private String map;
        private String phone;
        private String whatsapp;
        private String email;
        private String website;
        private Boolean isGhostKitchen;
        private Boolean hasDineIn;
        private Boolean isActive;
        private Integer whatsappClicks;

        public static EstablishmentDto fromEntity(Establishment e) {
            if (e == null) return null;
            return EstablishmentDto.builder()
                    .id(e.getEstablishmentId())
                    .establishmentId(e.getEstablishmentId())
                    .businessId(e.getBusiness() != null ? e.getBusiness().getBusinessId() : null)
                    .establishmentTypeId(e.getEstablishmentType() != null ? e.getEstablishmentType().getEstablishmentTypeId() : null)
                    .categoryName(e.getEstablishmentType() != null ? e.getEstablishmentType().getName() : null)
                    .name(e.getName())
                    .description(e.getDescription())
                    .schedules(e.getSchedules())
                    .neighborhood(e.getNeighborhood())
                    .address(e.getAddress())
                    .latitude(e.getLatitude())
                    .longitude(e.getLongitude())
                    .map(e.getMap())
                    .phone(e.getPhone())
                    .whatsapp(e.getWhatsapp() != null && !e.getWhatsapp().isBlank() ? e.getWhatsapp() : e.getPhone())
                    .email(e.getEmail())
                    .website(e.getWebsite())
                    .isGhostKitchen(e.getIsGhostKitchen())
                    .hasDineIn(e.getHasDineIn())
                    .isActive(e.getIsActive())
                    .whatsappClicks(e.getWhatsappClicks())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessDto {
        private Integer id;
        private Integer businessId;
        private Integer ownerUserId;
        private String ownerName;
        private String ownerEmail;
        private Integer establishmentTypeId;
        private String category;
        private String categoryEn;
        private String categoryIcon;
        private String name;
        private String legalName;
        private String taxNumber;
        private String description;
        private String descriptionEn;
        private String primaryColor;
        private String logoUrl;
        private String bannerUrl;
        private String neighborhood;
        private String address;
        private String phone;
        private String whatsapp;
        private String email;
        private String website;
        private Boolean isGhostKitchen;
        private Boolean hasDineIn;
        private Boolean isFeatured;
        private Boolean isActive;
        private Double ratingAvg;
        private Integer reviewCount;
        private Integer viewCount;
        private Integer searchAppearanceCount;
        private Integer whatsappClickCount;
        private List<EstablishmentDto> establishments;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;

        public static BusinessDto fromEntity(Business b) {
            if (b == null) return null;
            List<EstablishmentDto> branches = b.getEstablishments() != null
                    ? b.getEstablishments().stream().map(EstablishmentDto::fromEntity).toList()
                    : List.of();
            String mainNeighborhood = b.getNeighborhood();
            if ((mainNeighborhood == null || mainNeighborhood.isBlank()) && !branches.isEmpty()) {
                mainNeighborhood = branches.get(0).getNeighborhood();
            }
            String mainAddress = b.getAddress();
            if ((mainAddress == null || mainAddress.isBlank()) && !branches.isEmpty()) {
                mainAddress = branches.get(0).getAddress();
            }
            return BusinessDto.builder()
                    .id(b.getBusinessId())
                    .businessId(b.getBusinessId())
                    .ownerUserId(b.getOwner() != null ? b.getOwner().getUserId() : null)
                    .ownerName(b.getOwner() != null ? (b.getOwner().getName() + (b.getOwner().getLastName() != null ? " " + b.getOwner().getLastName() : "")) : null)
                    .ownerEmail(b.getOwner() != null ? b.getOwner().getEmail() : null)
                    .establishmentTypeId(b.getEstablishmentType() != null ? b.getEstablishmentType().getEstablishmentTypeId() : null)
                    .category(b.getEstablishmentType() != null ? b.getEstablishmentType().getName() : "General")
                    .categoryEn(b.getEstablishmentType() != null && b.getEstablishmentType().getNameEn() != null ? b.getEstablishmentType().getNameEn() : "General")
                    .categoryIcon(b.getEstablishmentType() != null ? b.getEstablishmentType().getIcon() : "store")
                    .name(b.getName())
                    .legalName(b.getLegalName())
                    .taxNumber(b.getTaxNumber())
                    .description(b.getDescription())
                    .descriptionEn(b.getDescriptionEn())
                    .primaryColor(b.getPrimaryColor() != null ? b.getPrimaryColor() : "#00A3FF")
                    .logoUrl(b.getLogoUrl())
                    .bannerUrl(b.getBannerUrl())
                    .neighborhood(mainNeighborhood)
                    .address(mainAddress)
                    .phone(b.getPhone())
                    .whatsapp(b.getWhatsapp())
                    .email(b.getEmail())
                    .website(b.getWebsite())
                    .isGhostKitchen(b.getIsGhostKitchen())
                    .hasDineIn(b.getHasDineIn())
                    .isFeatured(b.getIsFeatured())
                    .isActive(b.getIsActive())
                    .ratingAvg(b.getRatingAvg() != null ? Math.round(b.getRatingAvg() * 10.0) / 10.0 : 0.0)
                    .reviewCount(b.getReviewCount() != null ? b.getReviewCount() : 0)
                    .viewCount(b.getViewCount() != null ? b.getViewCount() : 0)
                    .searchAppearanceCount(b.getSearchAppearanceCount() != null ? b.getSearchAppearanceCount() : 0)
                    .whatsappClickCount(b.getWhatsappClickCount() != null ? b.getWhatsappClickCount() : 0)
                    .establishments(branches)
                    .createAt(b.getCreateAt())
                    .updateAt(b.getUpdateAt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessSaveRequest {
        private Integer ownerUserId;
        private Integer establishmentTypeId;
        private String name;
        private String legalName;
        private String taxNumber;
        private String description;
        private String descriptionEn;
        private String primaryColor;
        private String logoUrl;
        private String bannerUrl;
        private String neighborhood;
        private String address;
        private String phone;
        private String whatsapp;
        private String email;
        private String website;
        private Boolean isGhostKitchen;
        private Boolean hasDineIn;
        private Boolean isFeatured;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstablishmentSaveRequest {
        private Integer establishmentTypeId;
        private String name;
        private String description;
        private String schedules;
        private String neighborhood;
        private String address;
        private Double latitude;
        private Double longitude;
        private String phone;
        private String whatsapp;
        private String email;
        private String website;
        private Boolean isGhostKitchen;
        private Boolean hasDineIn;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginatedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
