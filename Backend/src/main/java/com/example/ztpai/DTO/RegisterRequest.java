package com.example.ztpai.DTO;

import jakarta.validation.constraints.NotEmpty;

public class RegisterRequest {
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
}
