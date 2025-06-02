package com.example.ztpai.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication token response")
public class JWTResponse {
    @Schema(description = "JWT access token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "JWT refresh token for obtaining new access tokens", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    public JWTResponse(String token, String JWTToken) {
        this.token = token;
        this.refreshToken = JWTToken;
    }

    public JWTResponse() {}

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
