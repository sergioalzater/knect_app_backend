package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.CmsSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CmsSectionRepository extends JpaRepository<CmsSection, Integer> {
    Optional<CmsSection> findByView_ViewIdAndNameIgnoreCase(Integer viewId, String name);
}
