package com.knect.knect_app_backend.controller;

import com.knect.knect_app_backend.dto.BusinessDtos.*;
import com.knect.knect_app_backend.dto.ReviewDtos.*;
import com.knect.knect_app_backend.entity.*;
import com.knect.knect_app_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DirectoryController {

    private final BusinessRepository businessRepository;
    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentTypeRepository establishmentTypeRepository;
    private final ReviewRepository reviewRepository;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final AnalyticsLogRepository analyticsLogRepository;

    @GetMapping("/categories")
    public List<EstablishmentType> getCategories() {
        return establishmentTypeRepository.findAll();
    }

    @GetMapping("/neighborhoods")
    public List<String> getNeighborhoods() {
        Set<String> neighborhoods = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        businessRepository.findByIsActiveTrueOrderByIsFeaturedDescRatingAvgDesc().forEach(b -> {
            if (b.getNeighborhood() != null && !b.getNeighborhood().isBlank()) {
                neighborhoods.add(b.getNeighborhood().trim());
            }
            if (b.getEstablishments() != null) {
                b.getEstablishments().forEach(e -> {
                    if (Boolean.TRUE.equals(e.getIsActive()) && e.getNeighborhood() != null && !e.getNeighborhood().isBlank()) {
                        neighborhoods.add(e.getNeighborhood().trim());
                    }
                });
            }
        });
        return new ArrayList<>(neighborhoods);
    }

    @GetMapping("/businesses/featured")
    public List<BusinessDto> getFeaturedBusinesses() {
        return businessRepository.findByIsActiveTrueAndIsFeaturedTrueOrderByRatingAvgDesc()
                .stream()
                .map(BusinessDto::fromEntity)
                .toList();
    }

    @GetMapping("/businesses")
    public List<BusinessDto> getBusinesses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isGhostKitchen,
            @RequestParam(required = false) Boolean featured
    ) {
        return filterBusinesses(search, neighborhood, categoryId, category, isGhostKitchen, featured);
    }

    @GetMapping("/businesses/catalog")
    public PaginatedResponse<BusinessDto> getBusinessesCatalog(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isGhostKitchen,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        List<BusinessDto> filtered = filterBusinesses(search, neighborhood, categoryId, category, isGhostKitchen, featured);
        int totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / Math.max(1, size));
        int safePage = Math.max(0, Math.min(page, Math.max(0, totalPages - 1)));
        int fromIndex = Math.min(safePage * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<BusinessDto> pageContent = filtered.subList(fromIndex, toIndex);

        return PaginatedResponse.<BusinessDto>builder()
                .content(pageContent)
                .page(safePage)
                .size(size)
                .totalElements(totalElements)
                .totalPages(Math.max(1, totalPages))
                .build();
    }

    private List<BusinessDto> filterBusinesses(
            String search,
            String neighborhood,
            Integer categoryId,
            String category,
            Boolean isGhostKitchen,
            Boolean featured
    ) {
        List<Business> allActive = businessRepository.findByIsActiveTrueOrderByIsFeaturedDescRatingAvgDesc();
        boolean hasSearchFilter = (search != null && !search.isBlank())
                || (neighborhood != null && !neighborhood.isBlank())
                || categoryId != null
                || (category != null && !category.isBlank());

        if (search != null && !search.isBlank()) {
            analyticsLogRepository.save(AnalyticsLog.builder()
                    .eventType("SEARCH")
                    .searchQuery(search.trim())
                    .build());
        }

        List<Business> matched = allActive.stream()
                .filter(b -> {
                    if (featured != null && featured && !Boolean.TRUE.equals(b.getIsFeatured())) {
                        return false;
                    }
                    if (categoryId != null) {
                        boolean matchCat = b.getEstablishmentType() != null
                                && categoryId.equals(b.getEstablishmentType().getEstablishmentTypeId());
                        if (!matchCat) return false;
                    }
                    if (category != null && !category.isBlank()) {
                        String catLower = category.trim().toLowerCase();
                        boolean matchCatName = b.getEstablishmentType() != null && (
                                (b.getEstablishmentType().getName() != null && b.getEstablishmentType().getName().toLowerCase().contains(catLower)) ||
                                (b.getEstablishmentType().getNameEn() != null && b.getEstablishmentType().getNameEn().toLowerCase().contains(catLower))
                        );
                        if (!matchCatName) return false;
                    }
                    if (isGhostKitchen != null) {
                        if (!isGhostKitchen.equals(Boolean.TRUE.equals(b.getIsGhostKitchen()))) {
                            return false;
                        }
                    }
                    if (neighborhood != null && !neighborhood.isBlank()) {
                        String nLower = neighborhood.trim().toLowerCase();
                        boolean inMain = b.getNeighborhood() != null && b.getNeighborhood().toLowerCase().contains(nLower);
                        boolean inBranch = b.getEstablishments() != null && b.getEstablishments().stream()
                                .anyMatch(e -> Boolean.TRUE.equals(e.getIsActive()) && e.getNeighborhood() != null && e.getNeighborhood().toLowerCase().contains(nLower));
                        if (!inMain && !inBranch) return false;
                    }
                    if (search != null && !search.isBlank()) {
                        String q = search.trim().toLowerCase();
                        boolean inName = b.getName() != null && b.getName().toLowerCase().contains(q);
                        boolean inDesc = (b.getDescription() != null && b.getDescription().toLowerCase().contains(q))
                                || (b.getDescriptionEn() != null && b.getDescriptionEn().toLowerCase().contains(q));
                        boolean inNeighborhood = b.getNeighborhood() != null && b.getNeighborhood().toLowerCase().contains(q);
                        boolean inCat = b.getEstablishmentType() != null && b.getEstablishmentType().getName() != null
                                && b.getEstablishmentType().getName().toLowerCase().contains(q);
                        boolean inBranch = b.getEstablishments() != null && b.getEstablishments().stream()
                                .anyMatch(e -> (e.getName() != null && e.getName().toLowerCase().contains(q))
                                        || (e.getNeighborhood() != null && e.getNeighborhood().toLowerCase().contains(q))
                                        || (e.getAddress() != null && e.getAddress().toLowerCase().contains(q)));
                        if (!inName && !inDesc && !inNeighborhood && !inCat && !inBranch) return false;
                    }
                    return true;
                })
                .toList();

        if (hasSearchFilter && !matched.isEmpty()) {
            matched.forEach(b -> b.setSearchAppearanceCount((b.getSearchAppearanceCount() != null ? b.getSearchAppearanceCount() : 0) + 1));
            businessRepository.saveAll(matched);
        }

        return matched.stream().map(BusinessDto::fromEntity).toList();
    }

    @GetMapping("/businesses/{id}")
    public BusinessDto getBusinessById(@PathVariable Integer id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado"));

        business.setViewCount((business.getViewCount() != null ? business.getViewCount() : 0) + 1);
        business = businessRepository.save(business);

        analyticsLogRepository.save(AnalyticsLog.builder()
                .eventType("BUSINESS_VIEW")
                .businessId(id)
                .build());

        return BusinessDto.fromEntity(business);
    }

    @PostMapping("/businesses/{id}/whatsapp-click")
    public Map<String, Object> registerWhatsappClick(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer establishmentId
    ) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado"));

        business.setWhatsappClickCount((business.getWhatsappClickCount() != null ? business.getWhatsappClickCount() : 0) + 1);
        businessRepository.save(business);

        if (establishmentId != null) {
            establishmentRepository.findById(establishmentId).ifPresent(est -> {
                est.setWhatsappClicks((est.getWhatsappClicks() != null ? est.getWhatsappClicks() : 0) + 1);
                establishmentRepository.save(est);
            });
        }

        analyticsLogRepository.save(AnalyticsLog.builder()
                .eventType("WHATSAPP_CLICK")
                .businessId(id)
                .establishmentId(establishmentId)
                .build());

        return Map.of(
                "success", true,
                "whatsappClickCount", business.getWhatsappClickCount()
        );
    }

    @GetMapping("/businesses/{id}/reviews")
    public List<ReviewDto> getBusinessReviews(@PathVariable Integer id) {
        return reviewRepository.findByBusiness_BusinessIdOrderByCreateAtDesc(id)
                .stream()
                .map(ReviewDto::fromEntity)
                .toList();
    }

    @PostMapping("/businesses/{id}/reviews")
    public ReviewDto createOrUpdateReview(
            @PathVariable Integer id,
            @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión para calificar un establecimiento");
        }
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comercio no encontrado"));

        int rating = request.getRating() != null ? Math.max(1, Math.min(5, request.getRating())) : 5;

        Establishment branch = null;
        if (request.getEstablishmentId() != null) {
            branch = establishmentRepository.findById(request.getEstablishmentId()).orElse(null);
        }

        // RF-12: Limitar a una reseña activa por usuario por local, con posibilidad de actualizarla
        Optional<Review> existingOpt = reviewRepository.findByBusiness_BusinessIdAndUser_UserId(id, user.getUserId());
        Review review;
        if (existingOpt.isPresent()) {
            review = existingOpt.get();
            review.setRating(rating);
            review.setComment(request.getComment());
            if (branch != null) {
                review.setEstablishment(branch);
            }
        } else {
            review = Review.builder()
                    .business(business)
                    .establishment(branch)
                    .user(user)
                    .rating(rating)
                    .comment(request.getComment())
                    .build();
        }

        review = reviewRepository.save(review);
        recalculateBusinessRating(business);

        return ReviewDto.fromEntity(review);
    }

    private void recalculateBusinessRating(Business business) {
        List<Review> allReviews = reviewRepository.findByBusiness_BusinessIdOrderByCreateAtDesc(business.getBusinessId());
        if (allReviews.isEmpty()) {
            business.setRatingAvg(0.0);
            business.setReviewCount(0);
        } else {
            double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            business.setRatingAvg(Math.round(avg * 10.0) / 10.0);
            business.setReviewCount(allReviews.size());
        }
        businessRepository.save(business);
    }

    @GetMapping("/businesses/{id}/media")
    public List<Media> getBusinessMedia(@PathVariable Integer id) {
        return mediaRepository.findByBusinessIdOrderByIsPrimaryDescCreateAtDesc(id);
    }

    @PostMapping("/businesses/{id}/media")
    public Media uploadBusinessMedia(
            @PathVariable Integer id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestBody(required = false) Map<String, String> body
    ) throws IOException {
        String url;
        String type = "IMAGE";
        if (file != null && !file.isEmpty()) {
            String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            url = "data:" + mimeType + ";base64," + base64;
        } else if (body != null && body.get("url") != null) {
            url = body.get("url");
            type = body.getOrDefault("type", "IMAGE");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere un archivo o URL");
        }

        Media media = Media.builder()
                .businessId(id)
                .url(url)
                .type(type)
                .isPrimary(false)
                .build();
        return mediaRepository.save(media);
    }
}
