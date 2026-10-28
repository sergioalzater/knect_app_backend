package com.knect.knect_app_backend.dto;

import com.knect.knect_app_backend.entity.Role;
import com.knect.knect_app_backend.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Integer id;
    private Integer userId;
    private String name;
    private String lastName;
    private String phone;
    private String email;
    private String role;
    private List<String> roles;
    private Boolean isActive;
    private LocalDateTime createAt;

    public static UserDto fromEntity(User user) {
        if (user == null) return null;
        List<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream().map(Role::getName).toList()
                : List.of("CLIENT");
        return UserDto.builder()
                .id(user.getUserId())
                .userId(user.getUserId())
                .name(user.getName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getPrimaryRoleName())
                .roles(roleNames)
                .isActive(user.getIsActive())
                .createAt(user.getCreateAt())
                .build();
    }
}
