package com.example.ztpai.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    private String sender_id;
    private String conversation_id;
    private String message;

    public Message(String sender_id, String conversation_id, String message) {
        this.sender_id = sender_id;
        this.conversation_id = conversation_id;
        this.message = message;
    }

    public String MessageInformation(){
        return "Message sent by: " + sender_id + " Converation_id: " + conversation_id + " Message: " + message;
    }
    @JsonProperty("sender_id")
    public String getSenderId() {
        return sender_id;
    }

    public void setSender_id(String sender_id){
        this.sender_id = sender_id;
    }

    public void setConversation_id(String conversation_id){
        this.conversation_id = conversation_id;
    }

    @JsonProperty("message_content")
    public String getMessageContent(){
        return message;
    }

    @JsonProperty("conversation_id")
    public String getConversationId(){
        return conversation_id;
    }
}
