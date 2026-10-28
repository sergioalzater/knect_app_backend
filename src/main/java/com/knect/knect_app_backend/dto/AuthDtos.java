package com.knect.knect_app_backend.dto;

import lombok.*;

public class AuthDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String name;
        private String lastName;
        private String phone;
        private String email;
        private String password;
        private String role; // optional: CLIENT or BUSINESS_ADMIN
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordRecoverRequest {
        private String email;
        private String newPassword;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String name;
        private String lastName;
        private String phone;
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponse {
        private String token;
        private UserDto user;
    }
}
