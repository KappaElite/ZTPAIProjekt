package com.example.ztpai.service;

import com.example.ztpai.model.User;
import com.example.ztpai.security.JWTUtil;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {
    private final JWTUtil jwtUtil;
    private final List<User> users = Arrays.asList(
            new User("admin", "admin", "ADMIN"),
            new User("user", "user", "USER")
            );
    public AuthService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    public String login(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .map(user -> jwtUtil.generateToken(user.getUsername()))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    public User getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
