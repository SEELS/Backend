package com.example.Repostitory;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.example.models.Notification;


public interface NotificationRepository extends CrudRepository<Notification, Long> {
	public ArrayList<Notification> findAllBySeen(boolean seen);

}
