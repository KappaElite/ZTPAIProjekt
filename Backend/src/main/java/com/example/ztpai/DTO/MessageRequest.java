package com.example.ztpai.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Message request payload for sending messages")
public class MessageRequest {
    @Schema(description = "Content of the message to be sent", example = "Hello, how are you today?")
    private String content;

    public MessageRequest() {}

    public MessageRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
