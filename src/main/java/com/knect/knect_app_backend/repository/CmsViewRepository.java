package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.CmsView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CmsViewRepository extends JpaRepository<CmsView, Integer> {
    Optional<CmsView> findByNameIgnoreCase(String name);
}
