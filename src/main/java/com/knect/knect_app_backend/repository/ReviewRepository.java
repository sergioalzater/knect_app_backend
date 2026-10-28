package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByBusiness_BusinessIdOrderByCreateAtDesc(Integer businessId);
    Optional<Review> findByBusiness_BusinessIdAndUser_UserId(Integer businessId, Integer userId);
}
