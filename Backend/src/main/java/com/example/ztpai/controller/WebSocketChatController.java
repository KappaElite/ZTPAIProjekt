package com.example.ztpai.controller;

import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.RabbitMQ.MessagesSender;
import com.example.ztpai.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@Tag(name = "WebSocket Chat", description = "WebSocket endpoints for real-time chat functionality")
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final MessagesSender messagesSender;
    public WebSocketChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService, MessagesSender messagesSender) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.messagesSender = messagesSender;
    }

    @MessageMapping("/chat")
    @Operation(summary = "Process private chat message",
               description = "WebSocket endpoint for processing and distributing private chat messages between users")
    public void processMessage(@Payload MessageDTO messageDTO) {
        messagesSender.send(messageDTO);
    }

    @MessageMapping("/groupChat")
    @PreAuthorize("hasAuthority('SUPERUSER')")
    @Operation(summary = "Process group chat message",
               description = "WebSocket endpoint for processing and distributing messages in group chats")
    public void processGroupMessage(@Payload GroupMessageDTO groupMessageDTO) {
        messagesSender.send(groupMessageDTO);
    }
}

