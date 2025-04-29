package com.example.ztpai.controller;


import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.DTO.MessageRequest;
import com.example.ztpai.model.Message;
import com.example.ztpai.repository.MessageRepository;
import com.example.ztpai.repository.UserRepository;
import com.example.ztpai.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Messages", description = "Messages operations")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/new/{sender_id}/{receiver_id}")
    @Operation(summary = "Adding message")
    public ResponseEntity newMessage(@RequestBody MessageRequest messageRequest, @PathVariable Long sender_id, @PathVariable Long receiver_id) {
        messageService.AddMesage(messageRequest.getContent(), sender_id, receiver_id);
        return ResponseEntity.status(201).body("Message added successfully");
    }

    @GetMapping("/get/{sender_id}/{receiver_id}")
    @Operation(summary = "Getting messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long sender_id, @PathVariable Long receiver_id) {
        List<MessageDTO> messages = messageService.getMessagesBetween(sender_id, receiver_id);
        return ResponseEntity.ok(messages);
    }
}
