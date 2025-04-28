package com.example.ztpai.ControrellersTests;

import com.example.ztpai.controller.MessageController;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.exception.message.MessageExceptions;
import com.example.ztpai.model.Message;
import com.example.ztpai.model.User;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import com.example.ztpai.service.AuthService;
import com.example.ztpai.service.MessageService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@Import({SecurityConfig.class, JWTUtil.class})
@AutoConfigureMockMvc(addFilters = false)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JWTFilter jwtFilter;

    @MockBean
    private MessageService messageService;

    private final Long validSenderId = 1L;
    private final Long validReceiverId = 2L;
    private final Long invalidUserId = 99L;
    private final String validContent = "Hello world";
    private final String emptyContent = "";

    @Nested
    @DisplayName("POST /api/chat/new/{sender_id}/{receiver_id} - Send message")
    class SendMessageTests {

        @Test
        @DisplayName("201 - Message sent successfully")
        void sendMessage_success() throws Exception {
            String requestBody = String.format("{\"content\":\"%s\"}", validContent);

            mockMvc.perform(post("/api/chat/new/{sender_id}/{receiver_id}", validSenderId, validReceiverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Message added successfully"));

            verify(messageService).AddMesage(validContent, validSenderId, validReceiverId);
        }

        @Test
        @DisplayName("400 - Empty message content")
        void sendMessage_emptyContent() throws Exception {
            String requestBody = String.format("{\"content\":\"%s\"}", emptyContent);

            doThrow(new MessageExceptions.MessageCannotBeEmptyException("Message cannot be empty"))
                    .when(messageService).AddMesage(emptyContent, validSenderId, validReceiverId);

            mockMvc.perform(post("/api/chat/new/{sender_id}/{receiver_id}", validSenderId, validReceiverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Message cannot be empty"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("404 - Sender not found")
        void sendMessage_senderNotFound() throws Exception {
            String requestBody = String.format("{\"content\":\"%s\"}", validContent);

            doThrow(new GlobalExceptions.UserNotFoundException("User not found"))
                    .when(messageService).AddMesage(validContent, invalidUserId, validReceiverId);

            mockMvc.perform(post("/api/chat/new/{sender_id}/{receiver_id}", invalidUserId, validReceiverId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("400 - Invalid path variables")
        void sendMessage_invalidPathVariables() throws Exception {
            String requestBody = String.format("{\"content\":\"%s\"}", validContent);

            mockMvc.perform(post("/api/chat/new/invalid/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/chat/get/{sender_id}/{receiver_id} - Get messages")
    class GetMessagesTests {

        @BeforeEach
        void setup() {
            User sender = new User();
            sender.setId(validSenderId);
            User receiver = new User();
            receiver.setId(validReceiverId);

            Message message1 = new Message(sender, receiver, "Hello");
            Message message2 = new Message(receiver, sender, "Hi there");
            List<Message> messages = Arrays.asList(message1, message2);

            when(messageService.getMessagesBetween(validSenderId, validReceiverId))
                    .thenReturn(messages);
        }

        @Test
        @DisplayName("200 - Get messages successfully")
        void getMessages_success() throws Exception {
            mockMvc.perform(get("/api/chat/get/{sender_id}/{receiver_id}", validSenderId, validReceiverId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].content").value("Hello"))
                    .andExpect(jsonPath("$[1].content").value("Hi there"));

            verify(messageService).getMessagesBetween(validSenderId, validReceiverId);
        }

        @Test
        @DisplayName("404 - User not found")
        void getMessages_userNotFound() throws Exception {
            doThrow(new GlobalExceptions.UserNotFoundException("User not found"))
                    .when(messageService).getMessagesBetween(invalidUserId, validReceiverId);

            mockMvc.perform(get("/api/chat/get/{sender_id}/{receiver_id}", invalidUserId, validReceiverId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("400 - Invalid path variables")
        void getMessages_invalidPathVariables() throws Exception {
            mockMvc.perform(get("/api/chat/get/invalid/2"))
                    .andExpect(status().isBadRequest());
        }
    }
}
