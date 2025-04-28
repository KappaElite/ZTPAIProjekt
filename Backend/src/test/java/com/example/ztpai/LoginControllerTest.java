package com.example.ztpai;

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

import static org.mockito.ArgumentMatchers.anyString;
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

    @Nested
    @DisplayName("POST /api/auth/login Positive scenarios")
    class SuccessfulLoginScenarios {

        @BeforeEach
        void setup() {
            User mockUser = new User();
            mockUser.setUsername(validUsername);
            mockUser.setPassword("$2a$10$hashedPassword");
            when(authService.login(validUsername, validPassword)).thenReturn(validToken);
        }

        @Test
        @DisplayName("201 - proper login with JWT return")
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
        @DisplayName("401 - incorrect password")
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

        @Test
        @DisplayName("400 - missing username")
        void no_username_login() throws Exception {

            String invalidRequestBody = "{\"password\":\"somePassword\"}";

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequestBody))
                            .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("400 - empty username")
    void empty_username_login() throws Exception {
        String requestBody = "{\"username\":\"\",\"password\":\"somePassword\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("400 - empty password")
    void no_password_login() throws Exception {
        String requestBody = "{\"username\":\"testUser\",\"password\":\"\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}