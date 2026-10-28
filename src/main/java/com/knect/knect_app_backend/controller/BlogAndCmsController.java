package com.knect.knect_app_backend.controller;

import com.knect.knect_app_backend.dto.BusinessDtos.PaginatedResponse;
import com.knect.knect_app_backend.dto.CmsAndAnalyticsDtos.*;
import com.knect.knect_app_backend.entity.*;
import com.knect.knect_app_backend.repository.*;
import com.knect.knect_app_backend.service.MailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogAndCmsController {

    private final BlogPostRepository blogPostRepository;
    private final CmsViewRepository cmsViewRepository;
    private final MailNotificationService mailNotificationService;
    private final AnalyticsLogRepository analyticsLogRepository;

    @Value("${app.contact.admin-email:administracion@knect.com}")
    private String adminEmail;

    @GetMapping("/blog-posts")
    public List<BlogPost> getPublishedBlogPosts() {
        return blogPostRepository.findByIsActiveTrueOrderByPublishedAtDesc();
    }

    @GetMapping("/blog-posts/paged")
    public PaginatedResponse<BlogPost> getPagedBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        List<BlogPost> all = blogPostRepository.findByIsActiveTrueOrderByPublishedAtDesc();
        int totalElements = all.size();
        int totalPages = (int) Math.ceil((double) totalElements / Math.max(1, size));
        int safePage = Math.max(0, Math.min(page, Math.max(0, totalPages - 1)));
        int fromIndex = Math.min(safePage * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        return PaginatedResponse.<BlogPost>builder()
                .content(all.subList(fromIndex, toIndex))
                .page(safePage)
                .size(size)
                .totalElements(totalElements)
                .totalPages(Math.max(1, totalPages))
                .build();
    }

    @GetMapping("/blog-posts/{idOrSlug}")
    public BlogPost getBlogPostDetail(@PathVariable String idOrSlug) {
        if (idOrSlug.matches("\\d+")) {
            return blogPostRepository.findById(Integer.parseInt(idOrSlug))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado"));
        }
        return blogPostRepository.findBySlug(idOrSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado"));
    }

    @GetMapping("/cms/home")
    public HomeCmsDto getHomeCms() {
        Map<String, String> values = new HashMap<>();
        cmsViewRepository.findByNameIgnoreCase("HOME").ifPresent(view -> {
            if (view.getSections() != null) {
                for (CmsSection sec : view.getSections()) {
                    if (sec.getSectionDataList() != null) {
                        for (CmsSectionData data : sec.getSectionDataList()) {
                            values.put(data.getName(), data.getContent());
                        }
                    }
                }
            }
        });

        return HomeCmsDto.builder()
                .heroTitleEs(values.getOrDefault("heroTitleEs", "Conectamos a Manizales con sus mejores comercios locales"))
                .heroTitleEn(values.getOrDefault("heroTitleEn", "Connecting Manizales with its best local businesses"))
                .heroSubtitleEs(values.getOrDefault("heroSubtitleEs", "Descubre restaurantes, cafés de especialidad, tiendas y servicios en El Cable, Milán, Palermo, Sancancio, Chipre y el Centro con ubicación interactiva y WhatsApp directo."))
                .heroSubtitleEn(values.getOrDefault("heroSubtitleEn", "Discover restaurants, specialty cafes, shops and services in El Cable, Milán, Palermo, Sancancio, Chipre and Downtown Manizales with interactive maps and direct WhatsApp."))
                .heroBannerUrl(values.getOrDefault("heroBannerUrl", "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1600&q=80"))
                .heroCtaTextEs(values.getOrDefault("heroCtaTextEs", "Explorar Directorio de Manizales"))
                .heroCtaTextEn(values.getOrDefault("heroCtaTextEn", "Explore Manizales Directory"))
                .featuredTitleEs(values.getOrDefault("featuredTitleEs", "Locales Destacados en Manizales"))
                .featuredTitleEn(values.getOrDefault("featuredTitleEn", "Featured Establishments in Manizales"))
                .newsTitleEs(values.getOrDefault("newsTitleEs", "Últimas Noticias y Novedades"))
                .newsTitleEn(values.getOrDefault("newsTitleEn", "Latest News & Updates"))
                .contactTitleEs(values.getOrDefault("contactTitleEs", "¿Tienes dudas o quieres registrar tu marca en Knect Manizales?"))
                .contactTitleEn(values.getOrDefault("contactTitleEn", "Have questions or want to list your brand on Knect Manizales?"))
                .contactSubtitleEs(values.getOrDefault("contactSubtitleEs", "Escríbenos y nuestro equipo administrativo se pondrá en contacto contigo de inmediato."))
                .contactSubtitleEn(values.getOrDefault("contactSubtitleEn", "Write to us and our administrative team will get in touch with you right away."))
                .contactEmail(values.getOrDefault("contactEmail", adminEmail))
                .contactPhone(values.getOrDefault("contactPhone", "+57 (606) 885 9000"))
                .contactAddress(values.getOrDefault("contactAddress", "Centro de Negocios Siglo XXI, Sector El Cable, Manizales, Caldas"))
                .build();
    }

    @PostMapping("/contact")
    public Map<String, Object> submitContactForm(@RequestBody ContactRequest request) {
        ContactMessage saved = mailNotificationService.processAndNotifyContact(request);

        return Map.of(
                "success", true,
                "contactId", saved.getContactId(),
                "notifiedTo", adminEmail,
                "message", "Tu mensaje ha sido enviado exitosamente y notificado por correo a la administración de Knect."
        );
    }

    @PostMapping("/analytics/visit")
    public Map<String, Object> recordVisit() {
        analyticsLogRepository.save(AnalyticsLog.builder()
                .eventType("VISIT")
                .build());
        return Map.of("recorded", true);
    }
}
