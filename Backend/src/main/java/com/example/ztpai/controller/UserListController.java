package com.example.ztpai.controller;


import com.example.ztpai.DTO.UserDTO;
import com.example.ztpai.service.UserListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/userlist")
public class UserListController {
    private final UserListService userListService;

    public UserListController(UserListService userListService) {
        this.userListService = userListService;
    }

    @GetMapping("/get/{user_id}")
    public ResponseEntity<List<UserDTO>> getUsers(@PathVariable Long user_id) {
        List<UserDTO> users = userListService.getAllUsers(user_id);
        return ResponseEntity.ok(users);
    }

}
