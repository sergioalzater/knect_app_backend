package com.knect.knect_app_backend.dto;

import com.knect.knect_app_backend.entity.Review;
import lombok.*;

import java.time.LocalDateTime;

public class ReviewDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewDto {
        private Integer id;
        private Integer reviewId;
        private Integer businessId;
        private String businessName;
        private Integer establishmentId;
        private String establishmentName;
        private Integer userId;
        private String userName;
        private Integer rating;
        private String comment;
        private String adminReply;
        private LocalDateTime adminReplyAt;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;

        public static ReviewDto fromEntity(Review r) {
            if (r == null) return null;
            String uName = "Cliente Knect";
            if (r.getUser() != null) {
                uName = r.getUser().getName() + (r.getUser().getLastName() != null ? " " + r.getUser().getLastName() : "");
            }
            return ReviewDto.builder()
                    .id(r.getReviewId())
                    .reviewId(r.getReviewId())
                    .businessId(r.getBusiness() != null ? r.getBusiness().getBusinessId() : null)
                    .businessName(r.getBusiness() != null ? r.getBusiness().getName() : null)
                    .establishmentId(r.getEstablishment() != null ? r.getEstablishment().getEstablishmentId() : null)
                    .establishmentName(r.getEstablishment() != null ? r.getEstablishment().getName() : null)
                    .userId(r.getUser() != null ? r.getUser().getUserId() : null)
                    .userName(uName)
                    .rating(r.getRating())
                    .comment(r.getComment())
                    .adminReply(r.getAdminReply())
                    .adminReplyAt(r.getAdminReplyAt())
                    .createAt(r.getCreateAt())
                    .updateAt(r.getUpdateAt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReviewRequest {
        private Integer establishmentId;
        private Integer rating;
        private String comment;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyReviewRequest {
        private String adminReply;
    }
}
