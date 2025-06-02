package com.example.ztpai.controller;

import com.example.ztpai.DTO.RequestDTO;
import com.example.ztpai.service.RequestService;
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

import java.util.List;

@RestController
@RequestMapping("/api/request")
@Tag(name = "Friend Requests", description = "Operations for managing friend requests including fetching, accepting, and rejecting requests")
public class RequestController {

    private final RequestService requestService;
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/get/{user_id}")
    @Operation(summary = "Get pending friend requests", description = "Retrieves all pending friend requests for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved friend requests",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RequestDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<RequestDTO>> getRequest(
            @Parameter(description = "ID of the user whose friend requests are being retrieved", required = true)
            @PathVariable Long user_id) {
        List<RequestDTO> requestDTOS = requestService.getRequest(user_id);
        return ResponseEntity.status(200).body(requestDTOS);
    }

    @PostMapping("/accept/{user_id}/{friend_id}")
    @Operation(summary = "Accept a friend request", description = "Accepts a pending friend request from another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Friend request accepted successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "404", description = "User, friend, or request not found")
    })
    public ResponseEntity<String> acceptRequest(
            @Parameter(description = "ID of the user accepting the friend request", required = true)
            @PathVariable Long user_id,
            @Parameter(description = "ID of the user who sent the friend request", required = true)
            @PathVariable Long friend_id) {
        requestService.acceptRequest(user_id, friend_id);
        return ResponseEntity.status(201).body("Successfully accepted request");
    }

    @PostMapping("/reject/{user_id}/{friend_id}")
    @Operation(summary = "Reject a friend request", description = "Rejects a pending friend request from another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Friend request rejected successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "404", description = "User, friend, or request not found")
    })
    public ResponseEntity<String> rejectRequest(
            @Parameter(description = "ID of the user rejecting the friend request", required = true)
            @PathVariable Long user_id,
            @Parameter(description = "ID of the user who sent the friend request", required = true)
            @PathVariable Long friend_id) {
        requestService.rejectRequest(user_id, friend_id);
        return ResponseEntity.status(201).body("Successfully rejected request");
    }
}
