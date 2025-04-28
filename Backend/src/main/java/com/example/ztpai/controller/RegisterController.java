package com.example.ztpai.controller;
import com.example.ztpai.DTO.LoginRequest;
import com.example.ztpai.DTO.RegisterRequest;
import com.example.ztpai.model.User;
import com.example.ztpai.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Register")
public class RegisterController {
    private final AuthService authService;
    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register")
    public ResponseEntity register( @Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
        return ResponseEntity.status(201).body("Register succes");
    }
}
