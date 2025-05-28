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
            "WHERE fr.senderId = :senderId AND fr.receiverId = :receiverId")
    boolean existsRequest(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    List<FriendRequest> findByReceiverId(Long receiverId);
}
