package com.knect.knect_app_backend.controller;

import com.knect.knect_app_backend.dto.AuthDtos.*;
import com.knect.knect_app_backend.dto.UserDto;
import com.knect.knect_app_backend.entity.Role;
import com.knect.knect_app_backend.entity.User;
import com.knect.knect_app_backend.repository.RoleRepository;
import com.knect.knect_app_backend.repository.UserRepository;
import com.knect.knect_app_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo y contraseña requeridos");
        }
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta de usuario se encuentra inactiva");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(UserDto.fromEntity(user))
                .build();
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos los campos obligatorios deben ser diligenciados");
        }

        String cleanEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está registrado");
        }

        String requestedRole = "BUSINESS_ADMIN".equalsIgnoreCase(request.getRole()) ? "BUSINESS_ADMIN" : "CLIENT";
        Role role = roleRepository.findByName(requestedRole)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(requestedRole)
                        .description("Rol " + requestedRole)
                        .build()));

        User user = User.builder()
                .name(request.getName().trim())
                .lastName(request.getLastName() != null ? request.getLastName().trim() : "")
                .phone(request.getPhone() != null ? request.getPhone().trim() : "")
                .email(cleanEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .roles(Set.of(role))
                .build();

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .user(UserDto.fromEntity(user))
                .build();
    }

    @PostMapping("/recover-password")
    public Map<String, Object> recoverPassword(@RequestBody PasswordRecoverRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico es obligatorio");
        }
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe una cuenta asociada a este correo"));

        String newPass = (request.getNewPassword() != null && !request.getNewPassword().isBlank())
                ? request.getNewPassword()
                : "Knect2026*";
        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Contraseña actualizada exitosamente. Ahora puedes iniciar sesión."
        );
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return UserDto.fromEntity(user);
    }

    @PutMapping("/profile")
    public UserDto updateProfile(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(newEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está en uso");
            }
            user.setEmail(newEmail);
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }
}
