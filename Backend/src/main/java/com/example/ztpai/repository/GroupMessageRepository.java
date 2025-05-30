package com.example.ztpai.repository;

import com.example.ztpai.model.LiveChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMessageRepository extends JpaRepository<LiveChat, Long> {

}
