package com.example.ztpai.service;

import com.example.ztpai.DTO.RequestDTO;
import com.example.ztpai.model.FriendRequest;
import com.example.ztpai.repository.NotificationRepository;
import com.example.ztpai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public RequestService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<RequestDTO> getRequest(Long userId) {
            List<FriendRequest> friendRequests = notificationRepository.findByReceiverId(userId);
            return friendRequests.stream()
                    .map(request -> new RequestDTO(request, userRepository))
                    .toList();
    }
}
