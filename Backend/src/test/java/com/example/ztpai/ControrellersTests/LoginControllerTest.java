package com.example.ztpai.ControrellersTests;

import com.example.ztpai.DTO.JWTResponse;
import com.example.ztpai.controller.LoginController;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.exception.auth.LoginExceptions;
import com.example.ztpai.model.User;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import com.example.ztpai.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import({SecurityConfig.class, JWTUtil.class})
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JWTFilter jwtFilter;

    private final String validUsername = "testUser";
    private final String validPassword = "testPassword";
    private final String invalidUsername = "nonExistingUser";
    private final String invalidPassword = "wrongPassword";
    private final String validToken = "valid.jwt.token";
    private final String validRefreshToken = "valid.refresh.token";

    @Nested
    @DisplayName("POST /api/auth/login Positive scenarios")
    class SuccessfulLoginScenarios {

        @BeforeEach
        void setup() {
            User mockUser = new User();
            mockUser.setUsername(validUsername);
            mockUser.setPassword("$2a$10$hashedPassword");
            when(authService.login(validUsername, validPassword))
                .thenReturn(new JWTResponse(validToken, validRefreshToken));
        }

        @Test
        @DisplayName("200 - proper login with JWT return")
        void proper_login() throws Exception {
            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                    validUsername, validPassword);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(validToken));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login Non positive scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("404 - user not found")
        void user_not_found_login() throws Exception {
            when(authService.login(invalidUsername, "testPassword"))
                    .thenThrow(new GlobalExceptions.UserNotFoundException("User not found"));

            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                    invalidUsername, "testPassword");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("401 - wrong password")
        void incorrect_password_login() throws Exception {
            User mockUser = new User();
            mockUser.setUsername(validUsername);
            mockUser.setPassword("$2a$10$hashedPassword");

            when(authService.login(validUsername, invalidPassword))
                    .thenThrow(new LoginExceptions.WrongPasswordException("Wrong password"));

            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                    validUsername, invalidPassword);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Wrong password"))
                    .andExpect(jsonPath("$.status").value(401));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login Validation scenarios")
    class ValidationScenarios {

        @Test
        @DisplayName("400 - Empty username")
        void empty_username_login() throws Exception {
            String requestBody = "{\"username\":\"\",\"password\":\"testPassword\"}";

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 - Empty password")
        void empty_password_login() throws Exception {
            String requestBody = "{\"username\":\"testUser\",\"password\":\"\"}";

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 - Missing username")
        void missing_username_login() throws Exception {
            String requestBody = "{\"password\":\"testPassword\"}";

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 - Missing password")
        void missing_password_login() throws Exception {
            String requestBody = "{\"username\":\"testUser\"}";

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 - Invalid JSON")
        void invalid_json_login() throws Exception {
            String requestBody = "{\"username\":\"testUser\",\"password\":}";

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }
}

