package com.example.ztpai.controller;


import com.example.ztpai.DTO.UserDTO;
import com.example.ztpai.service.UserListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/userlist")
@Tag(name = "Users", description = "Operations related to listing and managing users")
public class UserListController {
    private final UserListService userListService;

    public UserListController(UserListService userListService) {
        this.userListService = userListService;
    }

    @GetMapping("/get/{user_id}")
    @Operation(summary = "Get all available users", description = "Retrieves all users available to connect with, excluding the requesting user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<UserDTO>> getUsers(
            @Parameter(description = "ID of the user making the request (this user will be excluded from results)", required = true)
            @PathVariable Long user_id) {
        List<UserDTO> users = userListService.getAllUsers(user_id);
        return ResponseEntity.ok(users);
    }
}
