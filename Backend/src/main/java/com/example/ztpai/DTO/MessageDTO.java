package com.example.ztpai.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

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
