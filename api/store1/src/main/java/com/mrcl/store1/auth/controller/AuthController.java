package com.mrcl.store1.auth.controller;

import com.mrcl.store1.auth.dao.AppUserRepository;
import com.mrcl.store1.auth.dto.AuthResponse;
import com.mrcl.store1.auth.dto.LoginRequest;
import com.mrcl.store1.auth.dto.UserRegistrationRequest;
import com.mrcl.store1.auth.entity.AppUser;
import com.mrcl.store1.auth.security.JwtService;
import com.mrcl.store1.common.exception.InvalidCredentialsException;
import com.mrcl.store1.common.exception.ResourceConflictException;
import com.mrcl.store1.common.exception.ResourceForbiddenException;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String DEFAULT_USER_ROLE = "ROLE_USER";

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AppUserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody UserRegistrationRequest request) {
        validateEmailNotRegistered(request.email());

        AppUser user = buildUserFromRegistration(request);
        userRepository.save(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = findUserByEmail(request.email());

        validateUserEnabled(user);
        validatePassword(request.password(), user.getPasswordHash());

        String token = jwtService.generateToken(user.getEmail(), user.getRoles());
        return new AuthResponse(token);
    }

    private void validateEmailNotRegistered(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email already registered");
        }
    }

    private AppUser findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    }

    private void validateUserEnabled(AppUser user) {
        if (!user.isEnabled()) {
            throw new ResourceForbiddenException("User is blocked");
        }
    }

    private void validatePassword(String rawPassword, String passwordHash) {
        if (!passwordEncoder.matches(rawPassword, passwordHash)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    private AppUser buildUserFromRegistration(UserRegistrationRequest request) {
        AppUser user = new AppUser();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setRoles(Set.of(DEFAULT_USER_ROLE));
        user.setEnabled(true);
        return user;
    }
}