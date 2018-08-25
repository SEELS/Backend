package com.example.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.example.models.Notification;

import com.example.Repostitory.NotificationRepository;

@RestController
@CrossOrigin(origins = "*")
public class NotificationRestController {
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@RequestMapping(value = "/getUnseenNotification", method = RequestMethod.GET)
	public Map<String,Object> getUnseenNotification() {
		Map<String, Object> res = new HashMap<>();
		ArrayList<Notification> unseenNotifications=notificationRepository.findAllBySeen(false);
		if(unseenNotifications.size()==0)
		{
			res.put("Success", "Empty, all messages are seen!");
		}
		else{
			res.put("Success",unseenNotifications);

		}
		return res;
	}
	
	@RequestMapping(value = "/changeNotificationState/{ids}", method = RequestMethod.GET)
	public Map<String,Object> changeNotificationState(@PathVariable String ids) {
		boolean flag=true;
		Map<String, Object> res = new HashMap<>();
		String[] seenNotifications=ids.split(",");
		for(int i=0;i<seenNotifications.length;i++)
		{
			Notification notify=notificationRepository.findOne(Long.parseLong(seenNotifications[i]));
			if(notify==null)
			{
				flag=false;
				break;
			}
			else
			{
				notify.setSeen(true);
				if(notificationRepository.save(notify)==null)
				{
					flag=false;
					break;
				}
			}	
		}
		if(flag==false)
		{
			res.put("Error", "Connection Error");
		}
		else
		{
			res.put("Success", "All Notifications'state is changed");
		}
		return res;
	}
	
	

}
