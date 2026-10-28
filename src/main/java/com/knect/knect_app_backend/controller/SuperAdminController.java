package com.knect.knect_app_backend.controller;

import com.knect.knect_app_backend.dto.BusinessDtos.*;
import com.knect.knect_app_backend.dto.CmsAndAnalyticsDtos.*;
import com.knect.knect_app_backend.dto.UserDto;
import com.knect.knect_app_backend.entity.*;
import com.knect.knect_app_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final BusinessRepository businessRepository;
    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentTypeRepository establishmentTypeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BlogPostRepository blogPostRepository;
    private final CmsViewRepository cmsViewRepository;
    private final CmsSectionRepository cmsSectionRepository;
    private final CmsSectionDataRepository cmsSectionDataRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ReviewRepository reviewRepository;
    private final AnalyticsLogRepository analyticsLogRepository;

    @GetMapping("/businesses")
    public PaginatedResponse<BusinessDto> getAllBusinessesAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        List<Business> all = businessRepository.findAll();
        all.sort(Comparator.comparing(Business::getBusinessId).reversed());

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            all = all.stream().filter(b ->
                    (b.getName() != null && b.getName().toLowerCase().contains(q)) ||
                    (b.getNeighborhood() != null && b.getNeighborhood().toLowerCase().contains(q)) ||
                    (b.getEstablishmentType() != null && b.getEstablishmentType().getName().toLowerCase().contains(q)) ||
                    (b.getOwner() != null && b.getOwner().getEmail().toLowerCase().contains(q))
            ).toList();
        }

        int totalElements = all.size();
        int totalPages = (int) Math.ceil((double) totalElements / Math.max(1, size));
        int safePage = Math.max(0, Math.min(page, Math.max(0, totalPages - 1)));
        int fromIndex = Math.min(safePage * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<BusinessDto> content = all.subList(fromIndex, toIndex)
                .stream()
                .map(BusinessDto::fromEntity)
                .toList();

        return PaginatedResponse.<BusinessDto>builder()
                .content(content)
                .page(safePage)
                .size(size)
                .totalElements(totalElements)
                .totalPages(Math.max(1, totalPages))
                .build();
    }

    @PostMapping("/businesses")
    public BusinessDto createBusinessAdmin(@RequestBody BusinessSaveRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del comercio es obligatorio");
        }

        User owner = null;
        if (request.getOwnerUserId() != null) {
            owner = userRepository.findById(request.getOwnerUserId()).orElse(null);
        }

        EstablishmentType type = null;
        if (request.getEstablishmentTypeId() != null) {
            type = establishmentTypeRepository.findById(request.getEstablishmentTypeId()).orElse(null);
        }
        if (type == null) {
            type = establishmentTypeRepository.findAll().stream().findFirst().orElse(null);
        }

        Business business = Business.builder()
                .owner(owner)
                .establishmentType(type)
                .name(request.getName().trim())
                .legalName(request.getLegalName() != null ? request.getLegalName() : request.getName().trim() + " S.A.S.")
                .taxNumber(request.getTaxNumber() != null ? request.getTaxNumber() : "900.123.456-7")
                .description(request.getDescription())
                .descriptionEn(request.getDescriptionEn())
                .primaryColor(request.getPrimaryColor() != null ? request.getPrimaryColor() : "#00A3FF")
                .logoUrl(request.getLogoUrl())
                .bannerUrl(request.getBannerUrl())
                .neighborhood(request.getNeighborhood() != null ? request.getNeighborhood() : "El Cable")
                .address(request.getAddress() != null ? request.getAddress() : "Carrera 23 # 65-11, Manizales")
                .phone(request.getPhone())
                .whatsapp(request.getWhatsapp() != null ? request.getWhatsapp() : request.getPhone())
                .email(request.getEmail())
                .website(request.getWebsite())
                .isGhostKitchen(Boolean.TRUE.equals(request.getIsGhostKitchen()))
                .hasDineIn(request.getHasDineIn() == null || request.getHasDineIn())
                .isFeatured(Boolean.TRUE.equals(request.getIsFeatured()))
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();

        business = businessRepository.save(business);

        // Create default main establishment branch
        Establishment mainBranch = Establishment.builder()
                .business(business)
                .establishmentType(type)
                .name(business.getName() + " - Sede Principal")
                .description(business.getDescription())
                .schedules("Lunes a Domingo: 8:00 AM - 9:00 PM")
                .neighborhood(business.getNeighborhood())
                .address(business.getAddress())
                .latitude(5.0563)
                .longitude(-75.4864)
                .phone(business.getPhone())
                .whatsapp(business.getWhatsapp())
                .email(business.getEmail())
                .website(business.getWebsite())
                .isGhostKitchen(business.getIsGhostKitchen())
                .hasDineIn(business.getHasDineIn())
                .isActive(true)
                .build();
        establishmentRepository.save(mainBranch);
        business.getEstablishments().add(mainBranch);

        return BusinessDto.fromEntity(business);
    }

    @PutMapping("/businesses/{id}")
    public BusinessDto updateBusinessAdmin(
            @PathVariable Integer id,
            @RequestBody BusinessSaveRequest request
    ) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado"));

        if (request.getName() != null && !request.getName().isBlank()) business.setName(request.getName().trim());
        if (request.getLegalName() != null) business.setLegalName(request.getLegalName());
        if (request.getTaxNumber() != null) business.setTaxNumber(request.getTaxNumber());
        if (request.getDescription() != null) business.setDescription(request.getDescription());
        if (request.getDescriptionEn() != null) business.setDescriptionEn(request.getDescriptionEn());
        if (request.getPrimaryColor() != null) business.setPrimaryColor(request.getPrimaryColor());
        if (request.getLogoUrl() != null) business.setLogoUrl(request.getLogoUrl());
        if (request.getBannerUrl() != null) business.setBannerUrl(request.getBannerUrl());
        if (request.getNeighborhood() != null) business.setNeighborhood(request.getNeighborhood());
        if (request.getAddress() != null) business.setAddress(request.getAddress());
        if (request.getPhone() != null) business.setPhone(request.getPhone());
        if (request.getWhatsapp() != null) business.setWhatsapp(request.getWhatsapp());
        if (request.getEmail() != null) business.setEmail(request.getEmail());
        if (request.getWebsite() != null) business.setWebsite(request.getWebsite());
        if (request.getIsGhostKitchen() != null) business.setIsGhostKitchen(request.getIsGhostKitchen());
        if (request.getHasDineIn() != null) business.setHasDineIn(request.getHasDineIn());
        if (request.getIsFeatured() != null) business.setIsFeatured(request.getIsFeatured());
        if (request.getIsActive() != null) business.setIsActive(request.getIsActive());

        if (request.getEstablishmentTypeId() != null) {
            establishmentTypeRepository.findById(request.getEstablishmentTypeId())
                    .ifPresent(business::setEstablishmentType);
        }
        if (request.getOwnerUserId() != null) {
            userRepository.findById(request.getOwnerUserId()).ifPresent(owner -> {
                promoteUserToBusinessAdminIfNeeded(owner);
                business.setOwner(owner);
            });
        }

        Business savedBusiness = businessRepository.save(business);
        return BusinessDto.fromEntity(savedBusiness);
    }

    @PatchMapping("/businesses/{id}/toggle-featured")
    public BusinessDto toggleFeatured(@PathVariable Integer id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado"));
        business.setIsFeatured(!Boolean.TRUE.equals(business.getIsFeatured()));
        return BusinessDto.fromEntity(businessRepository.save(business));
    }

    @PatchMapping("/businesses/{id}/toggle-active")
    public BusinessDto toggleActive(@PathVariable Integer id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado"));
        business.setIsActive(!Boolean.TRUE.equals(business.getIsActive()));
        return BusinessDto.fromEntity(businessRepository.save(business));
    }

    @PutMapping("/businesses/{id}/owner")
    public BusinessDto transferOwnership(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body
    ) {
        Integer newOwnerId = body.get("ownerUserId");
        if (newOwnerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere ownerUserId");
        }
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado"));
        User newOwner = userRepository.findById(newOwnerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        promoteUserToBusinessAdminIfNeeded(newOwner);
        business.setOwner(newOwner);
        return BusinessDto.fromEntity(businessRepository.save(business));
    }

    private void promoteUserToBusinessAdminIfNeeded(User user) {
        boolean hasBusinessRole = user.getRoles().stream()
                .anyMatch(r -> "BUSINESS_ADMIN".equalsIgnoreCase(r.getName()) || "SUPER_ADMIN".equalsIgnoreCase(r.getName()));
        if (!hasBusinessRole) {
            roleRepository.findByName("BUSINESS_ADMIN").ifPresent(role -> {
                user.getRoles().add(role);
                userRepository.save(user);
            });
        }
    }

    @DeleteMapping("/businesses/{id}")
    public Map<String, Object> deleteBusiness(@PathVariable Integer id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado"));
        List<Review> reviews = reviewRepository.findByBusiness_BusinessIdOrderByCreateAtDesc(id);
        reviewRepository.deleteAll(reviews);
        businessRepository.delete(business);
        return Map.of("success", true, "deletedId", id);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    @PostMapping("/categories")
    public EstablishmentType createCategory(@RequestBody EstablishmentType category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoría es obligatorio");
        }
        return establishmentTypeRepository.save(category);
    }

    // RF-17: CMS de Página de Inicio
    @PutMapping("/cms/home")
    public Map<String, Object> updateHomeCms(@RequestBody Map<String, String> payload) {
        CmsView homeView = cmsViewRepository.findByNameIgnoreCase("HOME")
                .orElseGet(() -> cmsViewRepository.save(CmsView.builder()
                        .name("HOME")
                        .description("Configuración CMS de la Portada Principal de Knect")
                        .build()));

        CmsSection mainSection = cmsSectionRepository.findByView_ViewIdAndNameIgnoreCase(homeView.getViewId(), "MAIN_CONTENT")
                .orElseGet(() -> cmsSectionRepository.save(CmsSection.builder()
                        .view(homeView)
                        .name("MAIN_CONTENT")
                        .description("Datos editables del Home")
                        .build()));

        payload.forEach((key, value) -> {
            if (value != null) {
                CmsSectionData item = cmsSectionDataRepository
                        .findBySection_SectionIdAndNameIgnoreCase(mainSection.getSectionId(), key)
                        .orElseGet(() -> CmsSectionData.builder()
                                .section(mainSection)
                                .name(key)
                                .type("TEXT")
                                .build());
                item.setContent(value);
                cmsSectionDataRepository.save(item);
            }
        });

        return Map.of("success", true, "message", "CMS de la página de inicio actualizado correctamente");
    }

    // RF-19: CMS de Noticias / News
    @GetMapping("/blog-posts")
    public List<BlogPost> getAllBlogPostsAdmin() {
        return blogPostRepository.findAllByOrderByPublishedAtDesc();
    }

    @PostMapping("/blog-posts")
    public BlogPost createBlogPost(@RequestBody BlogPost post) {
        if (post.getTitle() == null || post.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título de la noticia es obligatorio");
        }
        String slug = post.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = post.getTitle().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .trim()
                    .replaceAll("\\s+", "-") + "-" + (System.currentTimeMillis() % 10000);
        }
        post.setSlug(slug);
        if (post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        if (post.getIsActive() == null) {
            post.setIsActive(true);
        }
        return blogPostRepository.save(post);
    }

    @PutMapping("/blog-posts/{id}")
    public BlogPost updateBlogPost(@PathVariable Integer id, @RequestBody BlogPost updated) {
        BlogPost existing = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Noticia no encontrada"));

        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getTitleEn() != null) existing.setTitleEn(updated.getTitleEn());
        if (updated.getSummary() != null) existing.setSummary(updated.getSummary());
        if (updated.getSummaryEn() != null) existing.setSummaryEn(updated.getSummaryEn());
        if (updated.getContent() != null) existing.setContent(updated.getContent());
        if (updated.getContentEn() != null) existing.setContentEn(updated.getContentEn());
        if (updated.getImageUrl() != null) existing.setImageUrl(updated.getImageUrl());
        if (updated.getAuthorName() != null) existing.setAuthorName(updated.getAuthorName());
        if (updated.getIsActive() != null) existing.setIsActive(updated.getIsActive());

        return blogPostRepository.save(existing);
    }

    @DeleteMapping("/blog-posts/{id}")
    public Map<String, Object> deleteBlogPost(@PathVariable Integer id) {
        blogPostRepository.deleteById(id);
        return Map.of("success", true, "deletedId", id);
    }

    // Contact messages management
    @GetMapping("/contact-messages")
    public List<ContactMessage> getContactMessages() {
        return contactMessageRepository.findAllByOrderByCreateAtDesc();
    }

    @PatchMapping("/contact-messages/{id}/read")
    public ContactMessage markMessageRead(@PathVariable Integer id) {
        ContactMessage msg = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensaje no encontrado"));
        msg.setIsRead(true);
        return contactMessageRepository.save(msg);
    }

    // RF-20: Dashboard Analítico Global
    @GetMapping("/analytics")
    public GlobalAnalyticsDto getGlobalAnalytics() {
        List<Business> allBusinesses = businessRepository.findAll();
        long totalViewsFromBusinesses = allBusinesses.stream()
                .mapToLong(b -> b.getViewCount() != null ? b.getViewCount() : 0)
                .sum();
        long totalWhatsappFromBusinesses = allBusinesses.stream()
                .mapToLong(b -> b.getWhatsappClickCount() != null ? b.getWhatsappClickCount() : 0)
                .sum();
        long visitLogs = analyticsLogRepository.countByEventType("VISIT");

        Map<String, Long> byCategory = allBusinesses.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getEstablishmentType() != null ? b.getEstablishmentType().getName() : "General",
                        Collectors.counting()
                ));

        List<String> recentSearches = analyticsLogRepository.findTop20ByEventTypeOrderByCreateAtDesc("SEARCH")
                .stream()
                .map(AnalyticsLog::getSearchQuery)
                .filter(Objects::nonNull)
                .distinct()
                .limit(10)
                .toList();

        List<BusinessDto> topWhatsapp = allBusinesses.stream()
                .sorted(Comparator.comparingInt((Business b) -> b.getWhatsappClickCount() != null ? b.getWhatsappClickCount() : 0).reversed())
                .limit(5)
                .map(BusinessDto::fromEntity)
                .toList();

        return GlobalAnalyticsDto.builder()
                .totalVisits(totalViewsFromBusinesses + visitLogs)
                .totalActiveBusinesses(businessRepository.countByIsActiveTrue())
                .totalBusinesses(allBusinesses.size())
                .totalBranches(establishmentRepository.count())
                .totalUsers(userRepository.count())
                .totalReviews(reviewRepository.count())
                .totalBlogPosts(blogPostRepository.count())
                .totalSearchQueries(analyticsLogRepository.countByEventType("SEARCH"))
                .totalWhatsappClicks(totalWhatsappFromBusinesses)
                .totalContactMessages(contactMessageRepository.count())
                .recentSearches(recentSearches)
                .businessesByCategory(byCategory)
                .topBusinessesByWhatsapp(topWhatsapp)
                .build();
    }
}
