package com.example.ztpai.controller;

import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.RabbitMQ.MessagesSender;
import com.example.ztpai.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
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
    public void processMessage(@Payload MessageDTO messageDTO) {
        messagesSender.send(messageDTO);
    }

    @MessageMapping("/groupChat")
    public void processGroupMessage(@Payload GroupMessageDTO groupMessageDTO) {
        messagesSender.send(groupMessageDTO);
    }
}

