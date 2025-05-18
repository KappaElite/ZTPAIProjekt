package com.example.ztpai.controller;

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
    private final MessagesSender sender;
    public WebSocketChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService, MessagesSender sender) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.sender = sender;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDTO messageDTO) {
        messageService.AddMesage(
            messageDTO.getContent(),
            messageDTO.getSender().getId(),
            messageDTO.getReceiver().getId()
        );

        sender.send();
        String destination = "/queue/messages/" + messageDTO.getReceiver().getId();
        messagingTemplate.convertAndSend(destination, messageDTO);
    }
}

