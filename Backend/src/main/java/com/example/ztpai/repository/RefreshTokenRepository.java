package com.example.ztpai.repository;

import com.example.ztpai.model.RefreshToken;
import com.example.ztpai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);
}
