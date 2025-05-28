package com.example.ztpai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
