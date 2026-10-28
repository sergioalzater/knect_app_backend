package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {
    List<BlogPost> findByIsActiveTrueOrderByPublishedAtDesc();
    List<BlogPost> findAllByOrderByPublishedAtDesc();
    Optional<BlogPost> findBySlug(String slug);
}
