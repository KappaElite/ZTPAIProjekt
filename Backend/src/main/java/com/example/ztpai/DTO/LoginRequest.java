package com.example.ztpai.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Login request data transfer object")
public class LoginRequest {
    @Schema(description = "Username for authentication", example = "john_doe", required = true)
    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @Schema(description = "User password", example = "password123", required = true)
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    public String getUsername() {return username;}
    public String getPassword() {return password;}
}
