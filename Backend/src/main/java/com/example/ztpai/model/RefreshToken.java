package com.example.ztpai.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "RefreshToken")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;


    public RefreshToken() {}

    public RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



}
