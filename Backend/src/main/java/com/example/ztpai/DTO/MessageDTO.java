package com.example.ztpai.DTO;

import java.time.LocalDateTime;

public class MessageDTO {
    private String content;
    private LocalDateTime sentAt;
    private UserDTO sender;
    private UserDTO receiver;

    public MessageDTO(String content, LocalDateTime sentAt, UserDTO sender, UserDTO receiver) {
        this.content = content;
        this.sentAt = sentAt;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public UserDTO getSender() {
        return sender;
    }

    public UserDTO getReceiver() {
        return receiver;
    }
}
