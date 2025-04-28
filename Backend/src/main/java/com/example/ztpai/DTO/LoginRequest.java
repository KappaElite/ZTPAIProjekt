package com.example.ztpai.DTO;

import jakarta.validation.constraints.NotEmpty;

public class LoginRequest {
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    public String getUsername() {return username;}
    public String getPassword() {return password;}
}
