package com.example.ztpai.DTO;

public class JWTResponse {
    private String token;
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
