package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Integer> {
    List<Media> findByBusinessIdOrderByIsPrimaryDescCreateAtDesc(Integer businessId);
    List<Media> findByPostIdOrderByIsPrimaryDescCreateAtDesc(Integer postId);
}
