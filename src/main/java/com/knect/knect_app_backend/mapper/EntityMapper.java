package com.knect.knect_app_backend.mapper;

import com.knect.knect_app_backend.dto.BusinessDtos.BusinessDto;
import com.knect.knect_app_backend.dto.BusinessDtos.EstablishmentDto;
import com.knect.knect_app_backend.dto.ReviewDtos.ReviewDto;
import com.knect.knect_app_backend.dto.UserDto;
import com.knect.knect_app_backend.entity.Business;
import com.knect.knect_app_backend.entity.Establishment;
import com.knect.knect_app_backend.entity.Review;
import com.knect.knect_app_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public UserDto toUserDto(User user) {
        return UserDto.fromEntity(user);
    }

    public BusinessDto toBusinessDto(Business business) {
        return BusinessDto.fromEntity(business);
    }

    public EstablishmentDto toEstablishmentDto(Establishment establishment) {
        return EstablishmentDto.fromEntity(establishment);
    }

    public ReviewDto toReviewDto(Review review) {
        return ReviewDto.fromEntity(review);
    }
}
