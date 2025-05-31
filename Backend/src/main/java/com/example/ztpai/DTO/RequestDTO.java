package com.example.ztpai.DTO;

import com.example.ztpai.model.FriendRequest;
import com.example.ztpai.model.User;
import com.example.ztpai.repository.UserRepository;

public class RequestDTO {
   private Long id;
   private String username;

    public RequestDTO() {}

    public RequestDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public RequestDTO(FriendRequest request, UserRepository userRepository) {
        User sender = userRepository.findById(request.getSender().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        this.id = request.getSender().getId();
        this.username = sender.getUsername();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

}
