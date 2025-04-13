package com.example.ztpai.DTO;

import com.example.ztpai.model.User;

public class FriendsDTO {
    private String username;
    private String email;

    public FriendsDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public FriendsDTO(User user) {
        username = user.getUsername();
        email = user.getEmail();
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
}
