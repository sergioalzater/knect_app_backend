package com.knect.knect_app_backend.service.impl;

import com.knect.knect_app_backend.dto.CmsAndAnalyticsDtos.ContactRequest;
import com.knect.knect_app_backend.entity.ContactMessage;
import com.knect.knect_app_backend.exception.BusinessRuleException;
import com.knect.knect_app_backend.repository.ContactMessageRepository;
import com.knect.knect_app_backend.service.MailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailNotificationServiceImpl implements MailNotificationService {

    private final ContactMessageRepository contactMessageRepository;

    @Value("${app.contact.admin-email:administracion@knect.com}")
    private String adminEmail;

    @Override
    @Transactional
    public ContactMessage processAndNotifyContact(ContactRequest request) {
        if (request.getName() == null || request.getName().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank() ||
                request.getSubject() == null || request.getSubject().isBlank() ||
                request.getMessage() == null || request.getMessage().isBlank()) {
            throw new BusinessRuleException("Todos los campos del formulario de contacto son obligatorios");
        }

        log.info("[KNECT-MAILER-SERVICE] Correo transaccional enviado a {} | Remitente: {} <{}> | Asunto: {}",
                adminEmail, request.getName(), request.getEmail(), request.getSubject());

        return contactMessageRepository.save(ContactMessage.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .subject(request.getSubject().trim())
                .message(request.getMessage().trim())
                .emailSent(true)
                .isRead(false)
                .build());
    }
}
