package com.example.ztpai.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Group message data transfer object")
public class GroupMessageDTO {
    @Schema(description = "Content of the message", example = "Hello everyone!")
    private String content;

    @Schema(description = "User who sent the message")
    private UserDTO sender;

    @Schema(description = "Date and time when message was sent", example = "2023-06-02T14:30:00")
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
