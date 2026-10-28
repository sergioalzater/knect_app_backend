package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.CmsSectionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CmsSectionDataRepository extends JpaRepository<CmsSectionData, Integer> {
    Optional<CmsSectionData> findBySection_SectionIdAndNameIgnoreCase(Integer sectionId, String name);
}
