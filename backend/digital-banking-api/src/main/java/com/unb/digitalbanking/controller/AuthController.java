package com.unb.digitalbanking.controller;

import com.unb.digitalbanking.dto.LoginRequest;
import com.unb.digitalbanking.dto.LoginResponse;
import com.unb.digitalbanking.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
}