package com.example.ztpai.service;

import com.example.ztpai.model.User;
import com.example.ztpai.security.JWTUtil;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {
    private final JWTUtil jwtUtil;

    private List<User> users = new ArrayList<>();

    public AuthService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        users.add(new User("admin", "admin", "ADMIN"));
        users.add(new User("user", "user", "USER"));
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

    public void register(String username, String password){
        //Metoda tymczasowa, brak połączaania z bazą danych
        users.add(new User(username, password, "USER"));
    }
}
