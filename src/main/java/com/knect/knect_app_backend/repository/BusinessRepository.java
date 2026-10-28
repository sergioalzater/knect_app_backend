package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Integer> {
    List<Business> findByIsActiveTrueOrderByIsFeaturedDescRatingAvgDesc();
    List<Business> findByIsActiveTrueAndIsFeaturedTrueOrderByRatingAvgDesc();
    List<Business> findByOwner_UserId(Integer userId);
    Optional<Business> findFirstByOwner_UserId(Integer userId);
    long countByIsActiveTrue();
}
