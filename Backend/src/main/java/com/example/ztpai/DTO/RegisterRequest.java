package com.example.ztpai.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "User registration request data")
public class RegisterRequest {
    @Schema(description = "Username for new account", example = "john_doe", required = true)
    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @Schema(description = "Password for new account", example = "securePassword123", required = true)
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @Schema(description = "Email address for new account", example = "john.doe@example.com", required = true)
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
}
