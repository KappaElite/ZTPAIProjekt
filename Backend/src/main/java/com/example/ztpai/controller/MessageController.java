package com.example.ztpai.controller;


import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.DTO.MessageRequest;
import com.example.ztpai.model.Message;
import com.example.ztpai.repository.MessageRepository;
import com.example.ztpai.repository.UserRepository;
import com.example.ztpai.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Messages", description = "Chat messaging operations including sending, receiving, and fetching group messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/new/{sender_id}/{receiver_id}")
    @Operation(summary = "Send a new message", description = "Sends a message from one user to another")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Message sent successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Invalid message data"),
        @ApiResponse(responseCode = "404", description = "Sender or receiver not found")
    })
    public ResponseEntity newMessage(
            @Parameter(description = "Message content", required = true)
            @RequestBody MessageRequest messageRequest,
            @Parameter(description = "ID of the message sender", required = true)
            @PathVariable Long sender_id,
            @Parameter(description = "ID of the message receiver", required = true)
            @PathVariable Long receiver_id) {
        messageService.AddMesage(messageRequest.getContent(), sender_id, receiver_id);
        return ResponseEntity.status(201).body("Message added successfully");
    }

    @GetMapping("/get/{sender_id}/{receiver_id}")
    @Operation(summary = "Get conversation messages", description = "Retrieves the message history between two users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved messages",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MessageDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<MessageDTO>> getMessages(
            @Parameter(description = "ID of the first user in the conversation", required = true)
            @PathVariable Long sender_id,
            @Parameter(description = "ID of the second user in the conversation", required = true)
            @PathVariable Long receiver_id) {
        List<MessageDTO> messages = messageService.getMessagesBetween(sender_id, receiver_id);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/groupchat/get")
    @Operation(summary = "Get group chat messages", description = "Retrieves all messages from group chats")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved group messages",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GroupMessageDTO.class))))
    })
    public ResponseEntity<List<GroupMessageDTO>> getGroupMessages() {
        List<GroupMessageDTO> groupMessages = messageService.getGroupMessages();
        return ResponseEntity.ok(groupMessages);
    }
}
