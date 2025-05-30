package com.example.ztpai.service;

import com.example.ztpai.DTO.RequestDTO;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.exception.friend.FriendExceptions;
import com.example.ztpai.model.FriendRequest;
import com.example.ztpai.model.User;
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
        if(!userRepository.existsById(userId)) {
            throw new GlobalExceptions.UserNotFoundException("User not found");
        }
        List<FriendRequest> friendRequests = notificationRepository.findByReceiverIdAndNotAccepted(userId);
        return friendRequests.stream()
                .map(request -> new RequestDTO(request, userRepository))
                .toList();
    }

    public void acceptRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptions.UserNotFoundException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new FriendExceptions.FriendNotFoundException("Friend not found"));
        FriendRequest friendRequest = notificationRepository.findByReceiverIdAndSenderId(userId, friendId)
                //Potencjlany wyjatek do dodania
                .orElseThrow(() -> new FriendExceptions.FriendshipAlreadyExistsException("Friendship not found"));

        if(friendRequest.isAccepted()){
            //Wyjatek do dodania
            throw new FriendExceptions.FriendshipAlreadyExistsException("Friendship already accepted");
        }
        friendRequest.setAccepted(true);
        user.getFriends().add(friend);
        friend.getFriends().add(user);
        userRepository.save(user);
        userRepository.save(friend);
        notificationRepository.save(friendRequest);
    }
    public void rejectRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptions.UserNotFoundException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new FriendExceptions.FriendNotFoundException("Friend not found"));
        FriendRequest friendRequest = notificationRepository.findByReceiverIdAndSenderId(userId, friendId)
                .orElseThrow(() -> new FriendExceptions.FriendshipAlreadyExistsException("Friendship not found"));

        if(friendRequest.isRejected()){
            throw new FriendExceptions.FriendshipAlreadyExistsException("Friendship already rejected");
        }
        friendRequest.setRejected(true);
        notificationRepository.save(friendRequest);
    }
}
