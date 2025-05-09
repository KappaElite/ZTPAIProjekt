package com.example.ztpai.service;

import com.example.ztpai.DTO.FriendsDTO;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.exception.friend.FriendExceptions;
import com.example.ztpai.model.User;
import com.example.ztpai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    private final UserRepository userRepository;

    public FriendsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void AddFriend(Long userId, Long friendID) {

        if(userId.equals(friendID)){
            throw new FriendExceptions.AddingYourselfException("You can't add yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptions.UserNotFoundException("User not found"));
        User friend = userRepository.findById(friendID)
                .orElseThrow(() -> new FriendExceptions.FriendNotFoundException("Friend not found"));

        if(userRepository.existsFriendship(userId, friendID)) {
            throw new FriendExceptions.FriendshipAlreadyExistsException("Friendship already exists");
        }

        //Tutaj do poprawki w przyszlosci, User dodajac kogoos jest z automatu przyjety przez druga strone
        //Ten sam problem bedzie z usuwaniem
        user.getFriends().add(friend);
        friend.getFriends().add(user);
        userRepository.save(user);
        userRepository.save(friend);
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
    }

    public List<FriendsDTO> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptions.UserNotFoundException("User not found"));
        return user.getFriends().stream()
                .map(FriendsDTO::new)
                .collect(Collectors.toList());
    }

}
