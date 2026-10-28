package com.knect.knect_app_backend.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

public class CmsAndAnalyticsDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HomeCmsDto {
        private String heroTitleEs;
        private String heroTitleEn;
        private String heroSubtitleEs;
        private String heroSubtitleEn;
        private String heroBannerUrl;
        private String heroCtaTextEs;
        private String heroCtaTextEn;
        private String featuredTitleEs;
        private String featuredTitleEn;
        private String newsTitleEs;
        private String newsTitleEn;
        private String contactTitleEs;
        private String contactTitleEn;
        private String contactSubtitleEs;
        private String contactSubtitleEn;
        private String contactEmail;
        private String contactPhone;
        private String contactAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactRequest {
        private String name;
        private String email;
        private String subject;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessAnalyticsDto {
        private Integer businessId;
        private String businessName;
        private Double ratingAvg;
        private Integer reviewCount;
        private Integer viewCount;
        private Integer searchAppearanceCount;
        private Integer whatsappClickCount;
        private int totalBranches;
        private int repliedReviewsCount;
        private int pendingReviewsCount;
        private List<BusinessDtos.EstablishmentDto> branchesPerformance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GlobalAnalyticsDto {
        private long totalVisits;
        private long totalActiveBusinesses;
        private long totalBusinesses;
        private long totalBranches;
        private long totalUsers;
        private long totalReviews;
        private long totalBlogPosts;
        private long totalSearchQueries;
        private long totalWhatsappClicks;
        private long totalContactMessages;
        private List<String> recentSearches;
        private Map<String, Long> businessesByCategory;
        private List<BusinessDtos.BusinessDto> topBusinessesByWhatsapp;
    }
}
