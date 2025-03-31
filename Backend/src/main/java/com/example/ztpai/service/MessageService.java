package com.example.ztpai.service;


import com.example.ztpai.model.Message;
import com.example.ztpai.model.User;
import com.example.ztpai.repository.MessageRepository;
import com.example.ztpai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void AddMesage(String content, Long senderId, Long receiverId) {
        if(content == null || content.trim().isEmpty()){
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if(sender.isEmpty() || receiver.isEmpty()){
            throw new IllegalArgumentException("Sender or Receiver not found");
        }
        Message message = new Message(sender.get(), receiver.get(), content);
        messageRepository.save(message);
    }

    public List<Message> getMessagesBetween(Long senderId, Long receiverId) {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if(sender.isEmpty() || receiver.isEmpty()){
            throw new IllegalArgumentException("Sender or Receiver not found");
        }
        return messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(senderId, receiverId, senderId, receiverId);
    }


}
