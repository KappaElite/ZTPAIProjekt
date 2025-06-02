package com.example.ztpai.controller;

import com.example.ztpai.DTO.FriendsDTO;
import com.example.ztpai.DTO.LoginRequest;
import com.example.ztpai.model.User;
import com.example.ztpai.service.AuthService;
import com.example.ztpai.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friend")
@Tag(name = "Friends", description = "Friend management operations including adding, removing, and listing friends")
public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @PostMapping("/add/{user_id}/{friend_id}")
    @Operation(summary = "Add a friend", description = "Creates a friendship connection between two users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Friend successfully added",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User or friend not found"),
        @ApiResponse(responseCode = "409", description = "Friendship already exists")
    })
    public ResponseEntity<String> addFriend(
            @Parameter(description = "ID of the user adding a friend", required = true) @PathVariable Long user_id,
            @Parameter(description = "ID of the user being added as friend", required = true) @PathVariable Long friend_id) {
        friendsService.AddFriend(user_id, friend_id);
        return ResponseEntity.status(201).body("Successfully add friend");
    }

    @DeleteMapping("/delete/{user_id}/{friend_id}")
    @Operation(summary = "Remove a friend", description = "Removes an existing friendship connection between two users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Friend successfully removed",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User, friend, or friendship not found")
    })
    public ResponseEntity<String> removeFriend(
            @Parameter(description = "ID of the user removing a friend", required = true) @PathVariable Long user_id,
            @Parameter(description = "ID of the user being removed as friend", required = true) @PathVariable Long friend_id) {
        friendsService.RemoveFriend(user_id, friend_id);
        return ResponseEntity.status(201).body("Successfully remove friend");
    }

    @GetMapping("/get/{user_id}")
    @Operation(summary = "Get list of friends", description = "Retrieves all friends of a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of friends list",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendsDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<FriendsDTO>> getFriends(
            @Parameter(description = "ID of the user whose friends are being retrieved", required = true) @PathVariable Long user_id) {
        List<FriendsDTO> friendsDTOS = friendsService.getFriends(user_id);
        return ResponseEntity.status(200).body(friendsDTOS);
    }
}
