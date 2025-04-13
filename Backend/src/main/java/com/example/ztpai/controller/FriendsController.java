package com.example.ztpai.controller;

import com.example.ztpai.DTO.LoginRequest;
import com.example.ztpai.model.User;
import com.example.ztpai.service.AuthService;
import com.example.ztpai.service.FriendsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/friend")

public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @PostMapping("/add/{user_id}/{friend_id}")
    public ResponseEntity<String> addFriend(@PathVariable Long user_id, @PathVariable Long friend_id) {
        try{
            friendsService.AddFriend(user_id, friend_id);
            return ResponseEntity.status(201).body("Successfully add friend");
        }catch(Exception e){
            return ResponseEntity.status(406).body("Add friend failed");
        }
    }

    @DeleteMapping("/delete/{user_id}/{friend_id}")
    public ResponseEntity<String> removeFriend(@PathVariable Long user_id, @PathVariable Long friend_id) {
        try{
            friendsService.RemoveFriend(user_id, friend_id);
            return ResponseEntity.status(201).body("Successfully remove friend");
        }
        catch(Exception e){
            return ResponseEntity.status(406).body("Remove friend failed");
        }
    }

}
