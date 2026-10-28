package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstablishmentRepository extends JpaRepository<Establishment, Integer> {
    List<Establishment> findByBusiness_BusinessIdOrderByEstablishmentIdAsc(Integer businessId);
}
