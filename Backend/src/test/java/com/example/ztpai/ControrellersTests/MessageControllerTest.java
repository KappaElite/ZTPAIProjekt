package com.example.ztpai.ControrellersTests;

import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.DTO.UserDTO;
import com.example.ztpai.controller.MessageController;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.exception.message.MessageExceptions;
import com.example.ztpai.model.User;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import com.example.ztpai.service.AuthService;
import com.example.ztpai.service.MessageService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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
    @DisplayName("GET /api/chat/get/{sender_id}/{receiver_id} - Get conversation messages")
    class GetMessagesTests {

        @Test
        @DisplayName("200 - Successfully retrieve conversation messages")
        void getMessages_success() throws Exception {
            UserDTO senderDTO = new UserDTO(validSenderId, "sender");
            UserDTO receiverDTO = new UserDTO(validReceiverId, "receiver");

            List<MessageDTO> messages = Arrays.asList(
                new MessageDTO("Hello", LocalDateTime.now(), senderDTO, receiverDTO),
                new MessageDTO("Hi there", LocalDateTime.now().plusMinutes(1), receiverDTO, senderDTO)
            );

            when(messageService.getMessagesBetween(validSenderId, validReceiverId)).thenReturn(messages);

            mockMvc.perform(get("/api/chat/get/{sender_id}/{receiver_id}", validSenderId, validReceiverId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].content").value("Hello"))
                    .andExpect(jsonPath("$[0].sender.id").value(validSenderId))
                    .andExpect(jsonPath("$[0].receiver.id").value(validReceiverId))
                    .andExpect(jsonPath("$[1].content").value("Hi there"))
                    .andExpect(jsonPath("$[1].sender.id").value(validReceiverId))
                    .andExpect(jsonPath("$[1].receiver.id").value(validSenderId));

            verify(messageService).getMessagesBetween(validSenderId, validReceiverId);
        }

        @Test
        @DisplayName("200 - No messages in conversation")
        void getMessages_emptyConversation() throws Exception {
            when(messageService.getMessagesBetween(validSenderId, validReceiverId))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/chat/get/{sender_id}/{receiver_id}", validSenderId, validReceiverId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(messageService).getMessagesBetween(validSenderId, validReceiverId);
        }

        @Test
        @DisplayName("404 - User not found")
        void getMessages_userNotFound() throws Exception {
            when(messageService.getMessagesBetween(invalidUserId, validReceiverId))
                    .thenThrow(new GlobalExceptions.UserNotFoundException("User not found"));

            mockMvc.perform(get("/api/chat/get/{sender_id}/{receiver_id}", invalidUserId, validReceiverId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(messageService).getMessagesBetween(invalidUserId, validReceiverId);
        }

        @Test
        @DisplayName("400 - Invalid path variables")
        void getMessages_invalidPathVariables() throws Exception {
            mockMvc.perform(get("/api/chat/get/invalid/2"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/chat/groupchat/get - Get group chat messages")
    class GetGroupMessagesTests {

        @BeforeEach
        void setup() {

            User mockUser = new User("admin", "hashedPassword", "admin@example.com", "SUPERUSER");
            mockUser.setId(1L);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockUser, null, Collections.singletonList(new SimpleGrantedAuthority("SUPERUSER"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        @AfterEach
        void cleanup() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("200 - Successfully retrieve group chat messages")
        @WithMockUser(roles = "SUPERUSER")
        void getGroupMessages_success() throws Exception {
            UserDTO user1 = new UserDTO(1L, "user1");
            UserDTO user2 = new UserDTO(2L, "user2");

            List<GroupMessageDTO> groupMessages = Arrays.asList(
                new GroupMessageDTO("Hello group", user1, LocalDateTime.now()),
                new GroupMessageDTO("Hi everyone", user2, LocalDateTime.now().plusMinutes(1))
            );

            when(messageService.getGroupMessages()).thenReturn(groupMessages);

            mockMvc.perform(get("/api/chat/groupchat/get"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].content").value("Hello group"))
                    .andExpect(jsonPath("$[0].sender.id").value(1))
                    .andExpect(jsonPath("$[1].content").value("Hi everyone"))
                    .andExpect(jsonPath("$[1].sender.id").value(2));

            verify(messageService).getGroupMessages();
        }

        @Test
        @DisplayName("200 - Empty group chat messages")
        @WithMockUser(roles = "SUPERUSER")
        void getGroupMessages_empty() throws Exception {
            when(messageService.getGroupMessages()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/chat/groupchat/get"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(messageService).getGroupMessages();
        }
    }
}
