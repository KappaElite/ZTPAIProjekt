package com.example.ztpai.service;

import com.example.ztpai.Repository.UserRepository;
import com.example.ztpai.model.User;
import com.example.ztpai.security.JWTUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public String login(String username, String password) {
       User user = userRepository.findByUsername(username)
               .orElseThrow(() -> new RuntimeException("User not found"));

       if(!passwordEncoder.matches(password, user.getPassword())) {
           throw new RuntimeException("Wrong password");
       }
       return jwtUtil.generateToken(username);
    }

    public void register(String username, String password, String email){
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already in use");
        }
        if(userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        String hashedPassword = passwordEncoder.encode(password);
        //Na razie domyslnie dodaje uzytkowanika o roli USER, na przyszlosc trzeba dodac inne
        User user = new User(username,hashedPassword,email,"USER");
        userRepository.save(user);
    }

    public User getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        return userRepository.findByUsername(username).orElse(null);
    }


}
