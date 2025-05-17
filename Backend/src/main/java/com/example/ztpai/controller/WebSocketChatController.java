package com.example.ztpai.controller;

import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.repository.UserRepository;
import com.example.ztpai.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserRepository userRepository;

    public WebSocketChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/send")
    public void processMessage(@Payload MessageDTO message, Principal principal) {
        String sender = principal.getName();
        System.out.println("Nazwa wysylajacego:" + sender);
        Long senderId= userRepository.findByUsername(sender).get().getId();
        messageService.AddMesage(message.getContent(), senderId, message.getReceiver().getId());
        messagingTemplate.convertAndSendToUser(
                message.getReceiver().getUsername(),
                "/queue/messages",
                message
        );
    }
}
