package com.example.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.example.Repostitory.DriverRepository;
import com.example.Repostitory.LocationRepository;
import com.example.Repostitory.NotificationRepository;
import com.example.Repostitory.TripLocationRepository;
import com.example.Repostitory.TripRepository;
import com.example.Repostitory.TruckRepository;
import com.example.models.Truck;
import com.example.models.Driver;
import com.example.models.Location;
import com.example.models.Notification;
import com.example.models.Trip;
import com.example.models.TripLocation;


@RestController
@CrossOrigin(origins = "*")
public class TruckRestController {
	@Autowired
	private TruckRepository truckRepository;

	@Autowired
	private TripLocationRepository tripLocationRepository;
	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private TripRepository tripRepository;

	@Autowired
	private NotificationRepository notificationRepository;

	/*
	 * it gets all the active trucks, helping to draw markers of all the active
	 * trucks
	 */
	@RequestMapping(value = "/getActiveTrucks", method = RequestMethod.GET)
	public Map<String,Object> getAllActiveTrucks() {
		Map<String, Object> res = new HashMap<>();
		if( truckRepository.findAllByActive(true)!=null)
		{
			res.put("Success",truckRepository.findAllByActive(true));
		}
		else
		{
			res.put("Error","There are no active trucks");
		}
		
		return res;
	}

	// sara & sameh Edit 3/4/2018 1:20 Dr :Shawky
	/* it gets an object of a specific truck, helping to get truck data */
	@RequestMapping(value = "/setDriverToTruck/{driver_id}/{Truck_id}", method = RequestMethod.GET)
	public Map<String, String> SetDriverToTruck(@PathVariable String Truck_id, @PathVariable long driver_id) {
		Map<String, String> res = new HashMap<>();
		Driver driver = driverRepository.findOne(driver_id);
		if (driver == null) {
			res.put("Error", "Driver Not found");
		}
		else 
		{
			if(driver.getDeleted()==true)
			{
				res.put("Error", "This driver is deleted");

			}
			else
			{
				Truck truck = truckRepository.findOne(Truck_id);
				if (truck == null) 
				{
					res.put("Error", "truck Not found");
				} 
				else 
				{
					if(truck.getDeleted()==true)
					{
						res.put("Error", "This truck is deleted");

					}
					else
					{
						truck.setDriver(driver);
						if (truckRepository.save(truck) != null)
							res.put("Success", "Done !!");
						else
							res.put("Error", "Connection Error");
					}
				}
			}
			
		}
		return res;
	}
	
	// sara & sameh Edit 3/4/2018 1:20 Dr :Shawky
	/* it gets an object of a specific truck, helping to get truck data */
	@RequestMapping(value = "/changeTruckState/{state}/{Truck_id}", method = RequestMethod.GET)
	public Map<String, String> changeTruckstate(@PathVariable String Truck_id, @PathVariable int state) {
		Map<String, String> res = new HashMap<>();
		Truck truck = truckRepository.findOne(Truck_id);
		if (truck == null) {
			res.put("Error", "Truck is Not found");
		}
		else 
		{
			if(state==1)
			{
				truck.setActive(true);
			}
			else
			{
				truck.setActive(false);
			}
			if (truckRepository.save(truck) == null) {
				res.put("Error", "Connection Error!!");
			} else
				res.put("Success", "Done !!");
		}
		return res;
	}

	/* it gets an object of a specific truck, helping to get truck data */
	@RequestMapping(value = "/viewTruck/{id}", method = RequestMethod.GET)
	public Map<String, Object> getSpecificTruck(@PathVariable String id) {
		Map<String, Object> res = new HashMap<>();
		Truck truck = truckRepository.findOne(id);
		if (truck == null)
			res.put("Error", "there's no truck with that Id");
		else
			res.put("Success", truck);
		return res;
	}
	
	//modified by Mariam
	/* it gets the actual current location of truck to draw a marker on the map */
	@RequestMapping(value = "/viewCurrentTruckLocation/{truck_id}", method = RequestMethod.GET)
	public Map<String, Object> vewCurrentTruckLocation(@PathVariable String truck_id) {
		Map<String, Object> res = new HashMap<>();
		Truck truck = truckRepository.findOne(truck_id);
		if (truck != null)
		{
			//res.put("Error","there's no truck with that Id");
			if(locationRepository.findFirstByTruckOrderByIdDesc(truck)!=null)
			{
				res.put("Success", locationRepository.findFirstByTruckOrderByIdDesc(truck));
			}
			else
			{
				res.put("Error","there's no location for this driver");
			}
		}   
		else {
			res.put("Error","there's no driver with that Id");
		}
		return res;
	}

	/* it gets all actual locations of a trip*/
	@RequestMapping(value = "/viewTripLocations/{trip_id}", method = RequestMethod.GET)
	public Map<String,Object> viewTripLocations(@PathVariable long trip_id) {
		Map<String, Object> res = new HashMap<>();
		if(tripRepository.findOne(trip_id)!=null)
		{
			Trip trip = tripRepository.findOne(trip_id);
			if(tripLocationRepository.findAllLByTrip(trip)!=null)
			{
				ArrayList<TripLocation> truckLocations = tripLocationRepository.findAllLByTrip(trip);
				ArrayList<Location> locations=new ArrayList<Location>();
				for(int i=0;i<truckLocations.size();i++)
				{
					locations.add(truckLocations.get(i).getLocation());
				}
				res.put("Success", locations);
			}
			else
			{
				res.put("Error", "There is no locations for this trip");
			}
		}
		else
		{
			res.put("Error", "There is no trip with this id");
		}
		return res;
	}
	
	/* it gets all actual locations of a truck*/
	@RequestMapping(value = "/viewTruckLocations/{truck_id}", method = RequestMethod.GET)
	public Map<String,Object> viewTrukLocations(@PathVariable String truck_id) {
		Map<String, Object> res = new HashMap<>();
		if(truckRepository.findOne(truck_id)!=null)
		{
			Truck truck = truckRepository.findOne(truck_id);
			Trip trip=new Trip();
			if(tripRepository.findByTruckAndState(truck,2)!=null)
			{
				trip = tripRepository.findByTruckAndState(truck,2);
				if(tripLocationRepository.findAllLByTrip(trip)!=null)
				{
					ArrayList<TripLocation> truckLocations = tripLocationRepository.findAllLByTrip(trip);
					ArrayList<Location> locations=new ArrayList<Location>();
					for(int i=0;i<truckLocations.size();i++)
					{
						locations.add(truckLocations.get(i).getLocation());
					}
					res.put("Success", locations);
				}
				else
					res.put("Error", "There are no locations for this trip");
			}
			else
				res.put("Error", "There is no trip with this truck and state");

		}else
			res.put("Error", "There is no truck with this id");
		
		return res;
	}
	
	@RequestMapping(value = "/viewDriverData/{truckId}", method = RequestMethod.GET)
	public Map<String, Object> getDriver(@PathVariable String truckId) {
		Map<String, Object> res = new HashMap<>();
		Truck truck = truckRepository.findOne(truckId);
		if (truck == null) {
			res.put("Error", "there's no truck with that Id");
		} else {
			if(truck.getActive()==false)
			{
				res.put("Error","This truck is not working yet");
			}
			else
			{
				Driver driver = truck.getDriver();
				if (driver == null)
					res.put("Error", "this truck not have Driver yet!!");
				else
					res.put("Success", driver);
			}
			
		}
		return res;
	}

	/*
	 * Deleted Driver when save truck not needed we can use assignDriverToTruck
	 * Service to assign that driver to truck Delete and Active when save truck not
	 * needed we can use changeTruckState To activate and deActivate
	 */

	@RequestMapping(value = "/{id}/saveTruck", method = RequestMethod.GET)
	public Map<String, String> saveTruck(@PathVariable String id) {

		Map<String, String> res = new HashMap<>();
		Truck truck_ = truckRepository.findOne(id);
		if (truck_ != null)
		{
			if (truck_.getDeleted()) {
				truck_.setDeleted(false);
				if (truckRepository.save(truck_) == null)
					res.put("Error", "Connection Error");
				else
					res.put("Success", "Done!");
			}
			else 
			{
				res.put("Error", "there's a Truck with that Id");
			}
		}
		else {
			Truck truck = new Truck();
			truck.setDriver(null);
			truck.setActive(false);
			truck.setId(id);
			truck.setDeleted(false);
			if (truckRepository.save(truck) == null)
				res.put("Error", "Connection Error");
			else
				res.put("Success", "Done!");
		}
		return res;
	}

	
	
	@Scheduled(fixedRate=60000)
	public void meetingPoint()
	{
		ArrayList<Truck> trucks = truckRepository.findAllByActive(true);
		for(int i=0;i<trucks.size();i++)
		{
			for(int j = i+1;j<trucks.size();j++)
			{
				changeInSpeed(trucks.get(i).getId(), trucks.get(j).getId());
				
			}
		}
	}
	
	/* modified by Mariam */
	//modified by Amina
	@RequestMapping(value = "/changeInSpeed/{truck1_id}/{truck2_id}", method = RequestMethod.GET)
	public Map<String,Object> changeInSpeed(@PathVariable String truck1_id, @PathVariable String truck2_id) {
		
		Map<String, Object> res = new HashMap<>();
		if(truckRepository.findOne(truck1_id)!=null)
		{
			Truck truck1 = truckRepository.findOne(truck1_id);
			if(truckRepository.findOne(truck2_id)!=null)
			{
				Truck truck2 = truckRepository.findOne(truck2_id);
				if(tripRepository.findByTruckAndState(truck1,2)!=null)
				{
					Driver driver1 = tripRepository.findByTruckAndState(truck1,2).getDriver();
					if( tripRepository.findByTruckAndState(truck2,2)!=null)
					{
						Driver driver2 = tripRepository.findByTruckAndState(truck2,2).getDriver();
						Location truckOneLocation = getCurrentLocation(driver1);
						if(truckOneLocation!=null)
						{
							Location truckTwoLocation = getCurrentLocation(driver2);
							if(truckTwoLocation!=null)
							{
								double truck1Speed = truckOneLocation.getSpeed();
								double truck2Speed = truckTwoLocation.getSpeed();
								if(truck1Speed!=0.0 && truck2Speed!=0.0)
								{
									double dist = getDistance(truckOneLocation, truckTwoLocation);
									String tokenDriver1=driver1.getToken();
									String tokenDriver2=driver2.getToken();
									Trip tripOne=tripRepository.findByTruckAndState(truck1,2);
									Trip tripTwo=tripRepository.findByTruckAndState(truck2,2);
									if(tokenDriver1!=null && tokenDriver2!=null )
									{
										String Message="";
										String Key="AIzaSyBrcdEhjh8S2NbfjCKzvUnxpK6PmiCYTfw";
										if (dist == 10)
										{
											Message="Be Careful Avoid Accidents";
											saveNotification(Message,tripOne.getTrip_id(), tripTwo.getTrip_id());

											send_FCM_Notification(tokenDriver1,Key,Message);
											send_FCM_Notification(tokenDriver2,Key,Message);
											res.put("Possible accident", "They will have an accident soon"); // Possible accident, distance <= 1km
											
										}

										else if (dist == 5)
										{
											Message="Be Careful On The Road";
											saveNotification(Message,tripOne.getTrip_id(), tripTwo.getTrip_id());
											send_FCM_Notification(tokenDriver1,Key,Message);
											send_FCM_Notification(tokenDriver2,Key,Message);
											res.put("Possible accident", "They will have an accident soon"); // Possible accident, distance <= 1km
											
											
											
										}
										else if (dist <= 2)
										{
											Message="Be Careful With Near Cars";
											saveNotification(Message,tripOne.getTrip_id(), tripTwo.getTrip_id());
											send_FCM_Notification(tokenDriver1,Key,Message);
											send_FCM_Notification(tokenDriver2,Key,Message);
											res.put("Possible accident", "They will have an accident soon"); // Possible accident, distance <= 1km
											
											
										}
										else 
										{	
											//by assuming they are on the same direction
											double Time = dist/1000;
											if(truck1Speed!=0 && truck2Speed!=0)
											{
												Time/=(truck1Speed+truck2Speed);
											}
											else if(truck1Speed==0 && truck1Speed!=0)
											{
												Time/=truck2Speed;
											}
											else if(truck2Speed==0 &&truck2Speed!=0 )
											{
												Time/=truck1Speed;
											}
											else 
												Time/=1;
											
											if(Time>1)
											{
												Message="There is a driver named "+driver2.getName() +" who you will meet after: "+Time +" hr";
												send_FCM_Notification(tokenDriver1,Key,Message);
												Message="there is a driver named "+driver1.getName() +" who you will meet after: "+Time +" hr";
												send_FCM_Notification(tokenDriver2,Key,Message);
												res.put("No possible accident", "They will meet after: "+Time+" hr" );
											}
											else
											{
												
												Time=Time*60;
												Message="there is a driver named "+driver2.getName() +" who you will meet after: "+Time +" Min";
												send_FCM_Notification(tokenDriver1,Key,Message);
												Message="there is a driver named "+driver1.getName() +" who you will meet after: "+Time+ " Min";
												send_FCM_Notification(tokenDriver2,Key,Message);
												res.put("No possible accident", "They will meet after: "+Time+"Min" );
											}
										}
										
										
//										else {
//											double newLat1 = truckOneLocation.getLat() + truck1Speed;
//											double newLon1 = truckOneLocation.getLon() + truck1Speed;
//											double newLat2 = truckTwoLocation.getLat() + truck1Speed;
//											double newLon2 = truckTwoLocation.getLon() + truck1Speed;
//											// System.out.println(newLat1 + "\t" + newLat2);
//											// System.out.println(newLon1 + "\t" + newLon2);
//											if (newLat2 >= newLat1 || newLon2 >= newLon1)
//											{
//												send_FCM_Notification(tokenDriver1,Key,Message);
//												send_FCM_Notification(tokenDriver2,Key,Message);
//												return true; // Possible Accident
//																// if truck2 speed: 50, location:60, truck1
//																// speed:30, location:120
//																// after 3 hrs the condition will be true that
//											}			// there'll be a possible clash
//										}
									}
									else
									{
										res.put("Error","there is no Tokens for these drivers :");
									}
								}
								else
								{
									res.put("Error","there is no value of speed for these trucks :");
								}
							}
							else
							{
								res.put("Error","there is no location for this truck :"+truck2_id);
							}
						}
						else
						{
							res.put("Error","there is no location for this truck :"+truck1_id);
						}
					}
					else
					{
						res.put("Error","there is no driver With this truck: "+truck2_id);
					}
				}
				else
				{
					res.put("Error","there is no driver for this truck: "+truck1_id);
				}
			}
			else
			{
				res.put("Error","there is no Truck With this id: "+truck2_id);
			}
		}
		else
		{
			res.put("Error","there is no Truck With this id: "+truck1_id);
		}
	
		return res;

	}

	private Location getCurrentLocation(Driver driver) {
		return locationRepository.findFirstByDriverOrderByIdDesc(driver);
	}

	/* modified by amina */
//	@RequestMapping(value = "/{truckId}/getCurrentTrip", method = RequestMethod.GET)
//	public Map<String, Object> getCurrentTrip(@PathVariable String truckId) {
//		Map<String, Object> res = new HashMap<>();
//
//		Truck truck = truckRepository.findOne(truckId);
//		if (truck == null) {
//			res.put("Error","there's no truck with that Id");
//		} else {
//
//			ArrayList<Trip> trips = (ArrayList<Trip>) tripRepository.findAll();
//			ArrayList<Trip> truckTrips = new ArrayList<Trip>();
//			for (int i = 0; i < trips.size(); i++) {
//				if (truck == trips.get(i).getTruck()) {
//					truckTrips.add(trips.get(i));
//				}
//			}
//			res.put("Success", truckTrips.get(truckTrips.size() - 1));
//		}
//
//		return res;
//	}


	// Modified by Randa
	@RequestMapping(value = "/{truckId}/getTruckTrip", method = RequestMethod.GET)
	public Map<String, Object> getTruckTrip(@PathVariable String truckId) {
		Map<String, Object> res = new HashMap<>();
		Truck truck = truckRepository.findOne(truckId);
		if (truck == null) {
			res.put("Error", "There's no Truck with that Id");
		} else {
			if(tripRepository.findByTruckAndState(truck,2)!=null)
			{
				Trip truckTrip = tripRepository.findByTruckAndState(truck,2);
				res.put("Success", truckTrip );
			}
			else
				res.put("Error", "There is no trip for this truck");
			
		}

		return res;
	}

	@RequestMapping(value = "/getTripTruck/{trip_id}", method = RequestMethod.GET)
	public Map<String, Object> getTripTruck(@PathVariable long trip_id) {
		Map<String, Object> res = new HashMap<>();
		Trip trip = tripRepository.findOne(trip_id);
		if (trip == null) {
			res.put("Error", "There's no Trip with this Id");
		} else {
			if(trip.getTruck()!=null)
			{
				if(trip.getState()==1)
				{
					res.put("Success" ,trip.getTruck().getId());
				    res.put("Values", "Created");    
				}
				else if(trip.getState()==0)
				{
					res.put("Success" ,trip.getTruck().getId());
					res.put("Values", "Completed");
				}
				else if(trip.getState()==2)
				{
					res.put("Success" ,trip.getTruck().getId());
					res.put("Values", "In Process" );
				}
				else
				{	res.put("Success" ,trip.getTruck().getId());
					res.put("Values", "The state of this trip is not correct" );
				}
			}
			else
				res.put("Error", "There is no trip for this truck");
			
		}

		return res;
	}
	@RequestMapping(value = "/getTruck/{truck_id}", method = RequestMethod.GET)
	public Map<String, Object> getTruck(@PathVariable String truck_id) {
		Map<String, Object> res = new HashMap<>();
		if (truckRepository.findOne(truck_id) == null) {
			res.put("Error", "there's no Truck with that Id");
		}
		else
		{
			Truck truck = truckRepository.findOne(truck_id);
			if (truck.getDeleted() == true) {
				res.put("Error", "this truck is deleted");
			} else
				res.put("Success", truck);
		}
		
		return res;
	}

	@RequestMapping(value = "/getAllTrucks", method = RequestMethod.GET)
	public Map<String,Object> getAllTrucks() {
		Map<String, Object> res = new HashMap<>();
		if(truckRepository.findByDeleted(false)!=null)
		{
			res.put("Success", truckRepository.findByDeleted(false));
		}
		else
			res.put("Error", "There is no trucks saved ");
		return res;
	}

	@RequestMapping(value = "/deleteAllTrucks", method = RequestMethod.GET)
	public Map<String, String> deleteAllTrucks() {
		Map<String, String> res = new HashMap<>();
		ArrayList<Truck> trucks = (ArrayList<Truck>) truckRepository.findAll();
		if(!trucks.isEmpty())
		{
			for (int i = 0; i < trucks.size(); i++) {
				trucks.get(i).setDeleted(true);
				if (truckRepository.save(trucks.get(i)) == null)
					res.put("Error", "Connection Error");
			}
			if (res.isEmpty())
				res.put("Success", "Trucks are Deleted!");
		}
		else
			res.put("Error","there is no Trucks saved");
		return res;
	}

	@RequestMapping(value = "/deleteTruck/{truck_id}", method = RequestMethod.GET)
	public Map<String, String> deleteTruck(@PathVariable String truck_id) {
		Map<String, String> res = new HashMap<>();
		if (truckRepository.findOne(truck_id) == null) {
			res.put("Error", "there's no Truck with That Id");
		} 
		else 
		{
			Truck truck = truckRepository.findOne(truck_id);
			truck.setDeleted(true);
			if (truckRepository.save(truck) != null)
					res.put("Success", "Truck is Deleted");
			else
				res.put("Error", "Connection Error");;
			
		}
		return res;
	}

	private double rad(double x) {
		return x * Math.PI / 180;
	}

	private double getDistance(Location p1, Location p2) {
		double R = 6378137; //
		double dLat = rad(p2.getLat() - p1.getLat());
		double dLong = rad(p2.getLon() - p1.getLon());
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(rad(p1.getLat())) * Math.cos(rad(p2.getLat())) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		return d; // return distance in meter
	}
	
	
	@RequestMapping(value = "/updateTruck/{truck_id}/{deleted}", method = RequestMethod.GET)
	public Map<String, String> updateTruck(@PathVariable String truck_id,@PathVariable int deleted) {
		Map<String, String> res = new HashMap<>();
		Truck truck = truckRepository.findOne(truck_id);
		if (truck == null) {
			res.put("Error", "there's no Truck with This Id");
		}
		else
		{
			if(deleted==1)
			{
				truck.setDeleted(true);
			}
			else
				truck.setDeleted(false);
			if (truckRepository.save(truck) != null)
				res.put("Success", "Truck is Updated");
			else
				res.put("Error", "Connection Error");
			
		}
		return res;
	}
	
	

@RequestMapping(value = "/send_FCM_Notification/{tokenId}/{server_key}/{message}", method = RequestMethod.GET)
	public Map<String,Object> send_FCM_Notification(@PathVariable String tokenId,@PathVariable String server_key,@PathVariable String message)
	{
		Map<String, Object> res = new HashMap<>();
		try{
		URL url = new URL("https://fcm.googleapis.com/fcm/send");
		HttpURLConnection conn;
		conn = (HttpURLConnection) url.openConnection();
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization","key="+server_key);
		conn.setRequestProperty("Content-Type","application/json");
		String Message="{\"notification\":{\"title\":\"Here is your notification.\",\"sound\":\"default\",\"body\":\""+message+"\"},\"to\":\""+tokenId+"\"}";
		System.out.println(Message);
//		JSONObject infoJson = new JSONObject();
//		infoJson.put("title","Here is your notification.");
//		infoJson.put("body", message);
//		JSONObject toJson = new JSONObject();
//		toJson.put("to",tokenId.trim());
//		toJson.put("notification", infoJson);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(Message);
		wr.flush();
		int status = 0;
		if( null != conn ){
		status = conn.getResponseCode();
		}
		if( status == 200 ){
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		res.put("Android Notification Response" , reader.readLine());
		}else if(status == 401){
		res.put("Notification Response TokenId",tokenId +"Error occurred");
		}else if(status == 501){
		res.put("Notification Response","[ errorCode=ServerError ] TokenId : " + tokenId);
		}else if( status == 503){
		res.put("Notification Response", "FCM Service is Unavailable  TokenId : " + tokenId);
		}
		else if( status != 0){
			res.put("Success", "There is no Errors!");
		}
			
		}
		catch(MalformedURLException mlfexception){
		res.put("Error occurred while sending push Notification!.." ,mlfexception.getMessage());
		}catch(IOException mlfexception){
		res.put("Reading URL, Error occurred while sending push Notification!.." , mlfexception.getMessage());
//		}catch(JSONException jsonexception){
//		System.out.println("Message Format, Error occurred while sending push Notification!.." + jsonexception.getMessage());
//		
		}catch (Exception exception) {
		res.put("Error occurred while sending push Notification!..",exception.getMessage());
		}
		return res;
		}
		public Map<String, String> saveNotification( String content, long trip1_id,long trip2_id ) {
			Map<String, String> res = new HashMap<>();
			Notification notification=new Notification();
			Trip tripOne=tripRepository.findOne(trip1_id);
			Trip tripTwo=tripRepository.findOne(trip2_id);
			notification.setTrip_id_1(tripOne);
		    notification.setTrip_id_2(tripTwo);
		    notification.setContent(content);
		    notification.setDeleted(false);
		    notification.setSeen(false);
		    if(notificationRepository.save(notification)!=null)
		    {
		    	res.put("Sccuess", "Done");
		    }
		    else
		    {
		    	res.put("Error", "Connection Error");
		    }
		    return res;
		}
	
}
