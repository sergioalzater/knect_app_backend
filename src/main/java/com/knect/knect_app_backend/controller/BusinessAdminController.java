package com.knect.knect_app_backend.controller;

import com.knect.knect_app_backend.dto.BusinessDtos.*;
import com.knect.knect_app_backend.dto.CmsAndAnalyticsDtos.BusinessAnalyticsDto;
import com.knect.knect_app_backend.dto.ReviewDtos.*;
import com.knect.knect_app_backend.entity.*;
import com.knect.knect_app_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/my-business")
@RequiredArgsConstructor
public class BusinessAdminController {

    private final BusinessRepository businessRepository;
    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentTypeRepository establishmentTypeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    private boolean isSuperAdmin(User user) {
        return user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> "SUPER_ADMIN".equalsIgnoreCase(r.getName()));
    }

    private Business resolveBusinessForUser(User user, Integer targetBusinessId) {
        if (isSuperAdmin(user)) {
            if (targetBusinessId != null) {
                return businessRepository.findById(targetBusinessId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado con ID: " + targetBusinessId));
            }
            // Si el Super Admin no especifica un targetBusinessId, toma el primer comercio activo del sistema
            return businessRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay comercios registrados en el sistema"));
        }

        // Si es un Administrador de Local Comercial, busca el comercio del cual es dueño
        return businessRepository.findFirstByOwner_UserId(user.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes ningún local comercial asignado a tu cuenta (" + user.getEmail() + ")"));
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }

    @GetMapping
    public BusinessDto getMyBusiness(
            Authentication authentication,
            @RequestParam(required = false) Integer businessId
    ) {
        User user = getAuthenticatedUser(authentication);
        Business business = resolveBusinessForUser(user, businessId);
        return BusinessDto.fromEntity(business);
    }

    @PutMapping
    public BusinessDto updateMyBusiness(
            Authentication authentication,
            @RequestParam(required = false) Integer businessId,
            @RequestBody BusinessSaveRequest request
    ) {
        User user = getAuthenticatedUser(authentication);
        Business business = resolveBusinessForUser(user, businessId);
        applyBusinessUpdates(business, request);
        business = businessRepository.save(business);
        return BusinessDto.fromEntity(business);
    }

    private void applyBusinessUpdates(Business business, BusinessSaveRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            business.setName(request.getName().trim());
        }
        if (request.getLegalName() != null) business.setLegalName(request.getLegalName().trim());
        if (request.getTaxNumber() != null) business.setTaxNumber(request.getTaxNumber().trim());
        if (request.getDescription() != null) business.setDescription(request.getDescription());
        if (request.getDescriptionEn() != null) business.setDescriptionEn(request.getDescriptionEn());
        if (request.getPrimaryColor() != null && !request.getPrimaryColor().isBlank()) {
            business.setPrimaryColor(request.getPrimaryColor().trim());
        }
        if (request.getLogoUrl() != null) business.setLogoUrl(request.getLogoUrl());
        if (request.getBannerUrl() != null) business.setBannerUrl(request.getBannerUrl());
        if (request.getNeighborhood() != null) business.setNeighborhood(request.getNeighborhood().trim());
        if (request.getAddress() != null) business.setAddress(request.getAddress().trim());
        if (request.getPhone() != null) business.setPhone(request.getPhone().trim());
        if (request.getWhatsapp() != null) business.setWhatsapp(request.getWhatsapp().trim());
        if (request.getEmail() != null) business.setEmail(request.getEmail().trim());
        if (request.getWebsite() != null) business.setWebsite(request.getWebsite().trim());
        if (request.getIsGhostKitchen() != null) business.setIsGhostKitchen(request.getIsGhostKitchen());
        if (request.getHasDineIn() != null) business.setHasDineIn(request.getHasDineIn());

        if (request.getEstablishmentTypeId() != null) {
            establishmentTypeRepository.findById(request.getEstablishmentTypeId())
                    .ifPresent(business::setEstablishmentType);
        }
    }

    @GetMapping("/establishments")
    public List<EstablishmentDto> getMyEstablishments(
            Authentication authentication,
            @RequestParam(required = false) Integer businessId
    ) {
        User user = getAuthenticatedUser(authentication);
        Business business = resolveBusinessForUser(user, businessId);
        return establishmentRepository.findByBusiness_BusinessIdOrderByEstablishmentIdAsc(business.getBusinessId())
                .stream()
                .map(EstablishmentDto::fromEntity)
                .toList();
    }

    @PostMapping("/establishments")
    public EstablishmentDto createEstablishment(
            Authentication authentication,
            @RequestParam(required = false) Integer businessId,
            @RequestBody EstablishmentSaveRequest request
    ) {
        User user = getAuthenticatedUser(authentication);
        Business business = resolveBusinessForUser(user, businessId);
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la sede es obligatorio");
        }

        EstablishmentType type = request.getEstablishmentTypeId() != null
                ? establishmentTypeRepository.findById(request.getEstablishmentTypeId()).orElse(business.getEstablishmentType())
                : business.getEstablishmentType();

        Establishment branch = Establishment.builder()
                .business(business)
                .establishmentType(type)
                .name(request.getName().trim())
                .description(request.getDescription())
                .schedules(request.getSchedules())
                .neighborhood(request.getNeighborhood())
                .address(request.getAddress())
                .latitude(request.getLatitude() != null ? request.getLatitude() : 5.0563)
                .longitude(request.getLongitude() != null ? request.getLongitude() : -75.4864)
                .phone(request.getPhone())
                .whatsapp(request.getWhatsapp() != null && !request.getWhatsapp().isBlank() ? request.getWhatsapp() : request.getPhone())
                .email(request.getEmail() != null ? request.getEmail() : business.getEmail())
                .website(request.getWebsite() != null ? request.getWebsite() : business.getWebsite())
                .isGhostKitchen(request.getIsGhostKitchen() != null ? request.getIsGhostKitchen() : business.getIsGhostKitchen())
                .hasDineIn(request.getHasDineIn() != null ? request.getHasDineIn() : business.getHasDineIn())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        branch = establishmentRepository.save(branch);
        return EstablishmentDto.fromEntity(branch);
    }

    @PutMapping("/establishments/{establishmentId}")
    public EstablishmentDto updateEstablishment(
            Authentication authentication,
            @PathVariable Integer establishmentId,
            @RequestBody EstablishmentSaveRequest request
    ) {
        User user = getAuthenticatedUser(authentication);
        Establishment branch = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sede no encontrada"));

        Business business = branch.getBusiness();
        if (!isSuperAdmin(user) && !business.getOwner().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para modificar esta sede");
        }

        if (request.getName() != null && !request.getName().isBlank()) branch.setName(request.getName().trim());
        if (request.getDescription() != null) branch.setDescription(request.getDescription());
        if (request.getSchedules() != null) branch.setSchedules(request.getSchedules());
        if (request.getNeighborhood() != null) branch.setNeighborhood(request.getNeighborhood());
        if (request.getAddress() != null) branch.setAddress(request.getAddress());
        if (request.getLatitude() != null) branch.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) branch.setLongitude(request.getLongitude());
        if (request.getPhone() != null) branch.setPhone(request.getPhone());
        if (request.getWhatsapp() != null) branch.setWhatsapp(request.getWhatsapp());
        if (request.getEmail() != null) branch.setEmail(request.getEmail());
        if (request.getWebsite() != null) branch.setWebsite(request.getWebsite());
        if (request.getIsGhostKitchen() != null) branch.setIsGhostKitchen(request.getIsGhostKitchen());
        if (request.getHasDineIn() != null) branch.setHasDineIn(request.getHasDineIn());
        if (request.getIsActive() != null) branch.setIsActive(request.getIsActive());

        branch = establishmentRepository.save(branch);
        return EstablishmentDto.fromEntity(branch);
    }

    @DeleteMapping("/establishments/{establishmentId}")
    public Map<String, Object> deleteEstablishment(
            Authentication authentication,
            @PathVariable Integer establishmentId
    ) {
        User user = getAuthenticatedUser(authentication);
        Establishment branch = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sede no encontrada"));

        Business business = branch.getBusiness();
        if (!isSuperAdmin(user) && !business.getOwner().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta sede");
        }

        business.getEstablishments().removeIf(e -> e.getEstablishmentId().equals(establishmentId));
        establishmentRepository.delete(branch);
        return Map.of("success", true, "deletedId", establishmentId);
    }

    @GetMapping("/reviews")
    public List<ReviewDto> getMyReviews(
            Authentication authentication,
            @RequestParam(required = false) Integer businessId
    ) {
        User user = getAuthenticatedUser(authentication);
        Business business = resolveBusinessForUser(user, businessId);
        return reviewRepository.findByBusiness_BusinessIdOrderByCreateAtDesc(business.getBusinessId())
                .stream()
                .map(ReviewDto::fromEntity)
                .toList();
    }

    @PutMapping("/reviews/{reviewId}/reply")
    public ReviewDto replyToReview(
            Authentication authentication,
            @PathVariable Integer reviewId,
            @RequestBody ReplyReviewRequest request
    ) {
        User user = getAuthenticatedUser(authentication);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada"));

        Business business = review.getBusiness();
        if (!isSuperAdmin(user) && !business.getOwner().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para responder reseñas de este local");
        }

        review.setAdminReply(request.getAdminReply());
        review.setAdminReplyAt(LocalDateTime.now());
        review = reviewRepository.save(review);
        return ReviewDto.fromEntity(review);
    }

    @GetMapping("/analytics")
    public BusinessAnalyticsDto getMyAnalytics(
            Authentication authentication,
            @RequestParam(required = false) Integer businessId
    ) {
        User user = getAuthenticatedUser(authentication);
        Business business = resolveBusinessForUser(user, businessId);
        List<Review> reviews = reviewRepository.findByBusiness_BusinessIdOrderByCreateAtDesc(business.getBusinessId());
        List<EstablishmentDto> branches = establishmentRepository
                .findByBusiness_BusinessIdOrderByEstablishmentIdAsc(business.getBusinessId())
                .stream()
                .map(EstablishmentDto::fromEntity)
                .toList();

        int repliedCount = (int) reviews.stream()
                .filter(r -> r.getAdminReply() != null && !r.getAdminReply().isBlank())
                .count();

        return BusinessAnalyticsDto.builder()
                .businessId(business.getBusinessId())
                .businessName(business.getName())
                .ratingAvg(business.getRatingAvg() != null ? Math.round(business.getRatingAvg() * 10.0) / 10.0 : 0.0)
                .reviewCount(reviews.size())
                .viewCount(business.getViewCount() != null ? business.getViewCount() : 0)
                .searchAppearanceCount(business.getSearchAppearanceCount() != null ? business.getSearchAppearanceCount() : 0)
                .whatsappClickCount(business.getWhatsappClickCount() != null ? business.getWhatsappClickCount() : 0)
                .totalBranches(branches.size())
                .repliedReviewsCount(repliedCount)
                .pendingReviewsCount(reviews.size() - repliedCount)
                .branchesPerformance(branches)
                .build();
    }
}
