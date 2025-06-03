package com.example.ztpai.service;

import com.example.ztpai.DTO.FriendsDTO;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.exception.friend.FriendExceptions;
import com.example.ztpai.model.FriendRequest;
import com.example.ztpai.model.User;
import com.example.ztpai.repository.NotificationRepository;
import com.example.ztpai.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public FriendsService(UserRepository userRepository, NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void AddFriend(Long userId, Long friendID) {

        if (userId.equals(friendID)) {
            throw new FriendExceptions.AddingYourselfException("You can't add yourself");
        }

        if (userRepository.existsFriendship(userId, friendID)) {
            throw new FriendExceptions.FriendshipAlreadyExistsException("Friendship already exists");
        }

        if (notificationRepository.existsRequest(userId, friendID)) {
            throw new FriendExceptions.FriendshipAlreadyExistsException("Friendship request already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptions.UserNotFoundException("User not found"));
        User friend = userRepository.findById(friendID)
                .orElseThrow(() -> new GlobalExceptions.UserNotFoundException("Friend not found"));

        notificationRepository.save(new FriendRequest(user, friend));
    }

    public void RemoveFriend(Long userId, Long friendID) {
        if(userId.equals(friendID)){
            throw new FriendExceptions.DeletingYourselfException("You can't delete yourself");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptions.UserNotFoundException("User not found"));
        User friend = userRepository.findById(friendID)
                .orElseThrow(() -> new FriendExceptions.FriendNotFoundException("Friend not found"));

        if(!userRepository.existsFriendship(userId, friendID)) {
            throw new FriendExceptions.FriendshipAlreadyDeletedException("Friendship already deleted");
        }
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        userRepository.save(user);
        userRepository.save(friend);
        String destination = "/topic/friends";
        messagingTemplate.convertAndSend(destination, "FriendChange");
    }

    public List<FriendsDTO> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptions.UserNotFoundException("User not found"));
        return user.getFriends().stream()
                .map(FriendsDTO::new)
                .collect(Collectors.toList());
    }

}
