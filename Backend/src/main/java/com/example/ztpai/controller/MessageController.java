package com.example.ztpai.controller;


import com.example.ztpai.DTO.MessageRequest;
import com.example.ztpai.model.Message;
import com.example.ztpai.repository.MessageRepository;
import com.example.ztpai.repository.UserRepository;
import com.example.ztpai.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/new/{sender_id}/{receiver_id}")
    public ResponseEntity newMessage(@RequestBody MessageRequest messageRequest, @PathVariable Long sender_id, @PathVariable Long receiver_id) {
        try{
            messageService.AddMesage(messageRequest.getContent(), sender_id, receiver_id);
            return ResponseEntity.status(201).body("Message added successfully");
        }
        catch (Exception e){
            return ResponseEntity.status(400).body("Error while adding new message");
        }
    }

    @GetMapping("/get/{sender_id}/{receiver_id}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long sender_id, @PathVariable Long receiver_id) {

        try{
            List<Message> messages = messageService.getMessagesBetween(sender_id, receiver_id);
            return ResponseEntity.ok(messages);
        }
        catch (Exception e){
            return ResponseEntity.status(400).body(new ArrayList<>());
        }

    }
}
