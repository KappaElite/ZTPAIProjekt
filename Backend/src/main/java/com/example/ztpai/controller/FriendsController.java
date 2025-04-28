package com.example.ztpai.controller;

import com.example.ztpai.DTO.FriendsDTO;
import com.example.ztpai.DTO.LoginRequest;
import com.example.ztpai.model.User;
import com.example.ztpai.service.AuthService;
import com.example.ztpai.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friend")
@Tag(name = "Friends", description = "Adding and deleting friends")

public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @PostMapping("/add/{user_id}/{friend_id}")
    @Operation(summary = "Adding friend")
    public ResponseEntity<String> addFriend(@PathVariable Long user_id, @PathVariable Long friend_id) {
        friendsService.AddFriend(user_id, friend_id);
        return ResponseEntity.status(201).body("Successfully add friend");
    }

    @DeleteMapping("/delete/{user_id}/{friend_id}")
    @Operation(summary = "Deleting friend")
    public ResponseEntity<String> removeFriend(@PathVariable Long user_id, @PathVariable Long friend_id) {
        friendsService.RemoveFriend(user_id, friend_id);
        return ResponseEntity.status(201).body("Successfully remove friend");
    }

    @GetMapping("/get/{user_id}")
    @Operation(summary = "Getting list of friends")
    public ResponseEntity<List<FriendsDTO>> getFriends(@PathVariable Long user_id) {
        List<FriendsDTO> friendsDTOS = friendsService.getFriends(user_id);
        return ResponseEntity.status(200).body(friendsDTOS);
    }

}
