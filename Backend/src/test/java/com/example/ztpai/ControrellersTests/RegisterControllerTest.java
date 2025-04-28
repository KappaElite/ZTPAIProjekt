package com.example.ztpai.ControrellersTests;

import com.example.ztpai.controller.RegisterController;
import com.example.ztpai.exception.auth.RegisterExceptions;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import com.example.ztpai.service.AuthService;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
@Import({SecurityConfig.class, JWTUtil.class})
@AutoConfigureMockMvc(addFilters = false)
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JWTFilter jwtFilter;

    private final String validUsername = "testUser";
    private final String validPassword = "testPassword";
    private final String validEmail = "test@gmail.com";
    private final String takenUsername = "user";
    private final String takenEmail = "user@gmail.com";

    @Nested
    @DisplayName("POST /api/auth/register Positive scenarios")
    class SuccessfulRegisterScenarios {
        @Test
        @DisplayName("201 - proper registration")
        void proper_register() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}",
                    validUsername, validPassword, validEmail
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Register succes"));

            verify(authService).register(validUsername, validPassword, validEmail);
        }
    }

    @Nested
    @DisplayName("POST /api/auth/register Non positive scenarios")
    class ExceptionScenarios {
        @Test
        @DisplayName("409 - username already taken")
        void username_taken() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}",
                    takenUsername, validPassword, validEmail
            );

            doThrow(new RegisterExceptions.UsernameTakenException("Username is already taken"))
                    .when(authService).register(takenUsername, validPassword, validEmail);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Username is already taken"))
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        @DisplayName("409 - email already taken")
        void email_taken() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}",
                    validUsername, validPassword, takenEmail
            );

            doThrow(new RegisterExceptions.EmailTakenException("Email already in use"))
                    .when(authService).register(validUsername, validPassword, takenEmail);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Email already in use"))
                    .andExpect(jsonPath("$.status").value(409));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/register Validation scenarios")
    class ValidationScenarios {
        @Test
        @DisplayName("400 - empty username")
        void empty_username() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"\",\"password\":\"%s\",\"email\":\"%s\"}",
                    validPassword, validEmail
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("400 - empty password")
        void empty_password() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"\",\"email\":\"%s\"}",
                    validUsername, validEmail
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("400 - empty email")
        void empty_email() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"\"}",
                    validUsername, validPassword
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("400 - missing username")
        void missing_username() throws Exception {
            String requestBody = String.format(
                    "{\"password\":\"%s\",\"email\":\"%s\"}",
                    validPassword, validEmail
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 - missing password")
        void missing_password() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"email\":\"%s\"}",
                    validUsername, validEmail
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 - missing email")
        void missing_email() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\"}",
                    validUsername, validPassword
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }
}