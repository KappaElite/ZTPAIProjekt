package com.example.ztpai.service;

import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.UserDTO;
import com.example.ztpai.model.User;
import com.example.ztpai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserListService {
    private final UserRepository userRepository;

    public UserListService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers(Long userId) {
        List<User> users = userRepository.findAllUsersExceptCurrentAndFriends(userId);
        return users.stream().map(user -> new UserDTO(
                user.getId(),
                user.getUsername()
        )).collect(Collectors.toList());
    }
}
