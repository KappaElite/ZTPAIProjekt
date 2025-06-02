package com.example.ztpai.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(String username, String role, Long userID) {

        String tmp = Jwts.builder()
                .setSubject(username)
                .claim("role",role)
                .claim("userID",userID)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
            return tmp;


    }

    public String generateRefreshToken(String username, Long userID) {
        String tmp = Jwts.builder()
                .setSubject(username)
                .claim("useID",userID)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
                .signWith(SECRET_KEY,SignatureAlgorithm.HS512)
                .compact();
        return tmp;
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
