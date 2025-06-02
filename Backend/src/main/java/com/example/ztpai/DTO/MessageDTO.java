package com.example.ztpai.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Message data transfer object for direct messages between users")
public class MessageDTO {
    @Schema(description = "Content of the message", example = "Hey, how are you?")
    private String content;

    @Schema(description = "Date and time when message was sent", example = "2023-06-02T14:30:00")
    private LocalDateTime sentAt;

    @Schema(description = "User who sent the message")
    private UserDTO sender;

    @Schema(description = "User who receives the message")
    private UserDTO receiver;

    public MessageDTO(String content, LocalDateTime sentAt, UserDTO sender, UserDTO receiver) {
        this.content = content;
        this.sentAt = sentAt;
        this.sender = sender;
        this.receiver = receiver;
    }

    public MessageDTO() {
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public void setReceiver(UserDTO receiver) {
        this.receiver = receiver;
    }

    public UserDTO getReceiver() {
        return receiver;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
