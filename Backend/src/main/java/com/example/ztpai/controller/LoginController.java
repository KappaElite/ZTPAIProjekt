package com.example.ztpai.controller;

import com.example.ztpai.DTO.JWTResponse;
import com.example.ztpai.DTO.LoginRequest;
import com.example.ztpai.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Login", description = "Authentication endpoints including login and token refresh")
public class LoginController {
    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates a user with username and password and returns JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JWTResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - incorrect login details")
    })
    public ResponseEntity<JWTResponse> login(
            @Parameter(description = "User login credentials", required = true)
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/refresh/{userID}")
    @Operation(summary = "Refresh access token", description = "Refreshes JWT token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JWTResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token or user ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - refresh token expired or invalid")
    })
    public ResponseEntity<JWTResponse> refresh(
            @Parameter(description = "Request body containing refresh token", required = true,
                    content = @Content(schema = @Schema(example = "{ \"token\": \"your_refresh_token_here\" }")))
            @RequestBody Map<String, String> request,
            @Parameter(description = "ID of the user requesting token refresh", required = true)
            @PathVariable Long userID) {

        String refreshToken = request.get("token");
        String newToken = authService.refreshToken(refreshToken, userID);

        JWTResponse jwtResponse = new JWTResponse(newToken, refreshToken);
        return ResponseEntity.ok(jwtResponse);
    }
}