package com.example.ztpai.service;


import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.DTO.UserDTO;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.exception.message.MessageExceptions;
import com.example.ztpai.model.LiveChat;
import com.example.ztpai.model.Message;
import com.example.ztpai.model.User;
import com.example.ztpai.repository.GroupMessageRepository;
import com.example.ztpai.repository.MessageRepository;
import com.example.ztpai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GroupMessageRepository groupMessageRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, GroupMessageRepository groupMessageRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupMessageRepository = groupMessageRepository;
    }

    public void AddMesage(String content, Long senderId, Long receiverId) {
        if(content == null || content.trim().isEmpty()){
            throw new MessageExceptions.MessageCannotBeEmptyException("Message cannot be empty");
        }
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if(sender.isEmpty() || receiver.isEmpty()){
            throw new GlobalExceptions.UserNotFoundException("User not found");
        }
        Message message = new Message(sender.get(), receiver.get(), content);
        messageRepository.save(message);
    }

    public void AddGroupMessage(String content, Long senderId) {
        if(content == null || content.trim().isEmpty()){
            throw new MessageExceptions.MessageCannotBeEmptyException("Message cannot be empty");
        }
        Optional<User> sender = userRepository.findById(senderId);

        if(sender.isEmpty()){
            throw new GlobalExceptions.UserNotFoundException("User not found");
        }
        LiveChat liveChat = new LiveChat(sender.get(), content);
        groupMessageRepository.save(liveChat);

    }


    public List<MessageDTO> getMessagesBetween(Long senderId, Long receiverId) {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if (sender.isEmpty() || receiver.isEmpty()) {
            throw new GlobalExceptions.UserNotFoundException("User not found");
        }

        List<Message> messages = messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(senderId, receiverId, senderId, receiverId);

        return messages.stream().map(message -> new MessageDTO(
                message.getContent(),
                message.getSentAt(),
                new UserDTO(message.getSender().getId(), message.getSender().getUsername()),
                new UserDTO(message.getReceiver().getId(), message.getReceiver().getUsername())
        )).collect(Collectors.toList());
    }

    public List<GroupMessageDTO> getGroupMessages(){
        List<LiveChat> groupMessages = groupMessageRepository.findAll();
        return groupMessages.stream().map(groupMessage -> new GroupMessageDTO(
                groupMessage.getContent(),
                new UserDTO(groupMessage.getSender().getId(), groupMessage.getSender().getUsername()),
                groupMessage.getSentAt()
        )).collect(Collectors.toList());
    }


}
