package com.example.ztpai.DTO;

import com.example.ztpai.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Friend user information")
public class FriendsDTO {
    @Schema(description = "Friend's user ID", example = "1")
    private Long id;

    @Schema(description = "Friend's username", example = "jane_doe")
    private String username;

    @Schema(description = "Friend's email address", example = "jane.doe@example.com")
    private String email;

    public FriendsDTO(String username, String email, Long id) {
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public FriendsDTO(User user) {
        username = user.getUsername();
        email = user.getEmail();
        id = user.getId();
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public Long getId() {
        return id;
    }
}
