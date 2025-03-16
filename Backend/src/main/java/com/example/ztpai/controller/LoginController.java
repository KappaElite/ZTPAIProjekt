package com.example.ztpai.controller;

import com.example.ztpai.DTO.LoginRequest;
import com.example.ztpai.model.User;
import com.example.ztpai.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class LoginController {
    private final AuthService authService;
    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request) {
        try{
            String token = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(token);
        }
        catch (Exception e){
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
