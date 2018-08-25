package com.example.Repostitory;

import org.springframework.data.repository.CrudRepository;

import com.example.models.Notification;


public interface NotificationRepository extends CrudRepository<Notification, Long> {

}
