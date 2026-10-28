package com.knect.knect_app_backend.repository;

import com.knect.knect_app_backend.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Integer> {
    List<ContactMessage> findAllByOrderByCreateAtDesc();
}
