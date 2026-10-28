package com.knect.knect_app_backend.service;

import com.knect.knect_app_backend.dto.CmsAndAnalyticsDtos.ContactRequest;
import com.knect.knect_app_backend.entity.ContactMessage;

public interface MailNotificationService {
    ContactMessage processAndNotifyContact(ContactRequest request);
}
