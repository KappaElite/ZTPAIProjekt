package com.example.ztpai.repository;

import com.example.ztpai.model.Message;
import com.example.ztpai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiver(User receiver);
    List<Message> findBySender(User sender);
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderBySentAtAsc(Long senderId, Long receiverId, Long receiverId2, Long senderId2);
}