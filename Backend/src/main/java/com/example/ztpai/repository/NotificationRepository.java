package com.example.ztpai.repository;

import com.example.ztpai.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.management.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END " +
            "FROM FriendRequest fr " +
            "JOIN User u ON fr.sender.id = u.id " +
            "WHERE fr.sender.id = :senderId AND fr.receiver.id = :receiverId")
    boolean existsRequest(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query("SELECT fr FROM FriendRequest fr  JOIN User u ON fr.receiver.id = u.id WHERE fr.receiver.id = :receiverId AND fr.accepted = false")
    List<FriendRequest> findByReceiverIdAndNotAccepted(@Param("receiverId") Long receiverId);

    Optional<FriendRequest> findByReceiverIdAndSenderId(Long receiverId, Long senderId);
}
