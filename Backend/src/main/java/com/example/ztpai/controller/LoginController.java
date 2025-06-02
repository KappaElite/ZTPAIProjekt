package com.example.ztpai.controller;

import com.example.ztpai.DTO.JWTResponse;
import com.example.ztpai.DTO.LoginRequest;
import com.example.ztpai.model.RefreshToken;
import com.example.ztpai.model.User;
import com.example.ztpai.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Login")
public class LoginController {
    private final AuthService authService;
    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Logging")
    public ResponseEntity <JWTResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/refresh/{userID}")
    public ResponseEntity<String> refresh(@Valid String refreshToken, @PathVariable Long userID) {
        String newToken = authService.refreshToken(refreshToken, userID);
        return ResponseEntity.ok(newToken);
    }
}
