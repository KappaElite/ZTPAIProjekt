package com.example.ztpai.ControrellersTests;

import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.DTO.UserDTO;
import com.example.ztpai.RabbitMQ.MessagesSender;
import com.example.ztpai.controller.WebSocketChatController;
import com.example.ztpai.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WebSocketChatControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MessagesSender messagesSender;

    private WebSocketChatController webSocketChatController;

    @BeforeEach
    void setup() {
        webSocketChatController = new WebSocketChatController(messagingTemplate, messageService, messagesSender);
    }

    @Nested
    @DisplayName("WebSocket /chat endpoint")
    class ProcessMessageTests {

        @Test
        @DisplayName("Successfully process private message")
        void shouldProcessPrivateMessage() {

            UserDTO sender = new UserDTO(1L, "sender");
            UserDTO receiver = new UserDTO(2L, "receiver");
            LocalDateTime now = LocalDateTime.now();

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setContent("Test message");
            messageDTO.setSender(sender);
            messageDTO.setReceiver(receiver);
            messageDTO.setSentAt(now);


            webSocketChatController.processMessage(messageDTO);


            ArgumentCaptor<MessageDTO> messageCaptor = ArgumentCaptor.forClass(MessageDTO.class);
            verify(messagesSender).send(messageCaptor.capture());
            MessageDTO capturedMessage = messageCaptor.getValue();

            assertEquals("Test message", capturedMessage.getContent());
            assertEquals(sender, capturedMessage.getSender());
            assertEquals(receiver, capturedMessage.getReceiver());
            assertEquals(now, capturedMessage.getSentAt());
        }
    }

    @Nested
    @DisplayName("WebSocket /groupChat endpoint")
    class ProcessGroupMessageTests {

        @Test
        @DisplayName("Successfully process group message")
        void shouldProcessGroupMessage() {

            UserDTO sender = new UserDTO(1L, "sender");
            LocalDateTime now = LocalDateTime.now();

            GroupMessageDTO groupMessageDTO = new GroupMessageDTO();
            groupMessageDTO.setContent("Test group message");
            groupMessageDTO.setSender(sender);
            groupMessageDTO.setSentAt(now);


            webSocketChatController.processGroupMessage(groupMessageDTO);


            ArgumentCaptor<GroupMessageDTO> messageCaptor = ArgumentCaptor.forClass(GroupMessageDTO.class);
            verify(messagesSender).send(messageCaptor.capture());
            GroupMessageDTO capturedMessage = messageCaptor.getValue();

            assertEquals("Test group message", capturedMessage.getContent());
            assertEquals(sender, capturedMessage.getSender());
            assertEquals(now, capturedMessage.getSentAt());
        }
    }
}
