package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.EstablishmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstablishmentTypeRepository extends JpaRepository<EstablishmentType, Integer> {
    Optional<EstablishmentType> findByNameIgnoreCase(String name);
}
