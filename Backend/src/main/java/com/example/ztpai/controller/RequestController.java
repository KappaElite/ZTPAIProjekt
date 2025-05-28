package com.example.ztpai.controller;

import com.example.ztpai.DTO.RequestDTO;
import com.example.ztpai.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request")
public class RequestController {

    private final RequestService requestService;
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/get/{user_id}")
    public ResponseEntity<List<RequestDTO>> getRequest(@PathVariable Long user_id) {
        List<RequestDTO> requestDTOS = requestService.getRequest(user_id);
        return ResponseEntity.status(200).body(requestDTOS);
    }

    @PostMapping("/accept/{user_id}/{friend_id}")
    public ResponseEntity<String> acceptRequest(@PathVariable Long user_id, @PathVariable Long friend_id) {
        requestService.acceptRequest(user_id, friend_id);
        return ResponseEntity.status(201).body("Successfully accepted request");
    }


}
