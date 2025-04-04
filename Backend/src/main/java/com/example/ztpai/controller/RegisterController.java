package com.example.ztpai.controller;
import com.example.ztpai.DTO.LoginRequest;
import com.example.ztpai.DTO.RegisterRequest;
import com.example.ztpai.model.User;
import com.example.ztpai.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {
    private final AuthService authService;
    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) {
        try{
            authService.register(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
            return ResponseEntity.status(201).body("Register succes");
        } catch (Exception e) {

            return ResponseEntity.status(406).body("Register failed");
        }
    }
}
