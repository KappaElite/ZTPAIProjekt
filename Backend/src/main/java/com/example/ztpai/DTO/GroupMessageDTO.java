package com.example.ztpai.DTO;

import java.time.LocalDateTime;

public class GroupMessageDTO {
    private String content;
    private UserDTO sender;
    private LocalDateTime sentAt;

    public GroupMessageDTO() {}

    public GroupMessageDTO(String content, UserDTO sender, LocalDateTime sentAt) {
        this.content = content;
        this.sender = sender;
        this.sentAt = sentAt;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public UserDTO getSender() {
        return sender;
    }
    public void setSender(UserDTO sender) {
        this.sender = sender;
    }
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
