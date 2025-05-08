package com.example.ztpai.DTO;

import com.example.ztpai.model.User;

public class FriendsDTO {
    private Long id;
    private String username;
    private String email;

    public FriendsDTO(String username, String email, Long id) {
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public FriendsDTO(User user) {
        username = user.getUsername();
        email = user.getEmail();
        id = user.getId();
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public Long getId() {
        return id;
    }
}
