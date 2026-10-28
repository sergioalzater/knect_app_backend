package com.knect.knect_app_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "media_id")
    private Integer mediaId;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(name = "title_en", length = 250)
    private String titleEn;

    @Column(nullable = false, unique = true, length = 250)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "summary_en", columnDefinition = "TEXT")
    private String summaryEn;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "content_en", columnDefinition = "LONGTEXT")
    private String contentEn;

    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(name = "author_name", length = 150)
    private String authorName;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        if (publishedAt == null) publishedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
