package com.example.RestController;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Repostitory.DriverRepository;
import com.example.Repostitory.LocationRepository;
import com.example.Repostitory.PenaltiesRepostitory;
import com.example.Repostitory.TripLocationRepository;
import com.example.Repostitory.TripRepository;
import com.example.Repostitory.TruckRepository;
import com.example.models.Driver;
import com.example.models.Location;
import com.example.models.Penalties;
import com.example.models.Trip;
import com.example.models.TripLocation;
import com.example.models.Truck;

@RestController
@CrossOrigin(origins = "*")

public class GPS {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private TruckRepository truckRepository;

	@Autowired
	private DriverRepository driverRepository;
	@Autowired
	private TripLocationRepository tripLocationRepository;
	
	
	@Autowired
	private TripRepository tripRepository;

	
	@Autowired
	private PenaltiesRepostitory penaltiesRepostitory;

	/* saving location with a specific driver and speed */
	// sara & sameh Edit 3/4/2018 1:20 Dr :Shawky
	//modified by Mariam
	//modified by sameh

	@RequestMapping(value = "/{lat}/{lon}/{speed}/{driver_id}/{tripId}/{road_id}/saveLocation", method = RequestMethod.GET)
	public Map<String, Object> saveLocation(@PathVariable Double lat, @PathVariable Double lon,
			@PathVariable Double speed, @PathVariable long driver_id,@PathVariable long tripId,@PathVariable long road_id) {
		Map<String, Object> res = new HashMap<>();
		Location l = new Location();
		l.setLat(lat);
		l.setLon(lon);
		l.setSpeed(speed);
		Date date = new Date();
		l.setTime(date);
		Trip trip = new Trip();
		if(tripRepository.findOne(tripId)!=null)
			trip=tripRepository.findOne(tripId);
		else
			res.put("Error", "trip is not found");
		
		if(road_id!=0)
		{
			l.setRoad(trip.getRoad());
		}
		else
		{
			l.setRoad(null);
		}
		Driver driver = driverRepository.findOne(driver_id);
		if (driver == null) 
		{
			res.put("Error", "Driver is not found");
		} 
		else 
		{
			l.setDriver(driver);
			if(trip.getDriver()==driver)
			{
				Truck truck = trip.getTruck();
				if (truck == null) 
				{
					res.put("Error", "Driver's truck is not found !!");
				} 
				else 
				{
					l.setTruck(truck);
					if(locationRepository.save(l)==null)
					{
						res.put("Error","Connection Error");
					}
					else 
					{
						truck.setPreviousSpeed(truck.getCurrentSpeed());
						truck.setCurrentSpeed(speed);
						if(truckRepository.save(truck) == null)
						{
							res.put("Error","Connection Error");
						}
						else
						{
						
							TripLocation tripLocation = new TripLocation();
							tripLocation.setLocation(l);
							tripLocation.setTrip(trip);
							if(tripLocationRepository.save(tripLocation)!=null)
								res.put("Success", "location is added");
							else
								res.put("Error", "Connection Error");

						}
						
					}
				}
				
			}
			else
			{
				{
					res.put("Error","There is no trip with this driver");
				}
			}
			
//			DriverRestController d=new DriverRestController();
//			Map<String,Object> p=d.calculateBrakePenalty(truck.getPreviousSpeed(), truck.getCurrentSpeed(), tripId);
//			p=d.calculateSpeedPenalty(tripId);
//			if (p.containsKey("driver total rate is"))
//			{
//				res.put("rate: ", p.get("driver total rate is"));
//			}
//			else {
//				res.put("Error in rate", p.get("Error"));
//			}
		}
		return res;
	}

	/*
	 * get current location of the recent trips. it will get the active location of
	 * trips
	 */
	@RequestMapping(value = "/getCurrentLocation", method = RequestMethod.GET)
	public Map<String ,Object> getCurrentLocation() {
		Map<String, Object> res = new HashMap<>();
		ArrayList<Location> location= (ArrayList<Location>)locationRepository.findAll();
		if(location!=null)
		{
			res.put("Success", location.get(location.size()-1));
		}
		else
		{
			res.put("Error", "There are no locations saved!");
		}
		return res;
	}

	@RequestMapping(value="/getAllLocations",method=RequestMethod.GET)
	public Map<String,Object> getAllLocations()
	{
		Map<String, Object> res = new HashMap<>();
		if(locationRepository.findAllByDeleted(false)!=null)
		{
			ArrayList<Location> locations=(ArrayList<Location>)locationRepository.findAllByDeleted(false);
			res.put("Success", locations);
		}
		else
		{
			res.put("Error", "There are no locations saved!");
		}
		return res;
	}
	
	@RequestMapping(value="/getLocation/{location_id}",method=RequestMethod.GET)
	public Map<String ,Object> getLocation(@PathVariable long location_id)
	{
		Map<String, Object> res = new HashMap<>();
		if(locationRepository.findOne(location_id)==null)
		{
			res.put("Error", "There is no location with id!");
			
		}
		else
		{
			Location location =locationRepository.findOne(location_id);
			if(location.getDeleted()==true)
			{
				res.put("Error","There location is deleted!" );
			}
			else
			{
				res.put("Success", location);
			}
		}
		
		return res;
	}
	
	@RequestMapping(value="/deleteAllLocations",method=RequestMethod.GET)
	public Map<String,Object> deleteAllLocations()
	{
		boolean flag=true;
		Map<String, Object> res = new HashMap<>();
		if(locationRepository.findAll()==null)
		{
			res.put("Error","There are no locations saved!");
		}
		else
		{
			ArrayList<Location> locations= (ArrayList<Location>)locationRepository.findAll();
			for(int i=0;i<locations.size();i++)
			{
				locations.get(i).setDeleted(true);
				if(locationRepository.save(locations.get(i))==null)
					flag=false;
			}
			if(flag==false)
			{
				res.put("Error", "Connection Error!");
			}
			else
			{
				res.put("Success","All locations are deleted!");
			}
		}
		return res;
	}
	
	@RequestMapping(value="/deleteLocation/{Location_id}",method=RequestMethod.GET)
	public Map<String,Object> deleteLocation(@PathVariable long Location_id)
	{
		Map<String, Object> res = new HashMap<>();
		if(locationRepository.findOne(Location_id)==null)
		{
			res.put("Error","There is no Location With this id!");
		}
		else
		{
			Location location=locationRepository.findOne(Location_id);
			if(location.getDeleted()==false)
			{
				location.setDeleted(true);
				if(locationRepository.save(location)!=null)
					res.put("Success", "Location is deleted");
				else
					res.put("Error", "Connection Error");
			}
			else
			{
				res.put("Success","This Location is already deleted");
			}
			
		}
		return res;
	}
	
	public double rate(long tripId) {
		if(tripRepository.findOne(tripId)!=null);
		Trip trip = tripRepository.findOne(tripId);
		Driver driver = trip.getDriver();
		double tripRate = 5.0;
		ArrayList<Penalties> ps = penaltiesRepostitory.findByTrip(trip);
		for (int i = 0; i < ps.size(); i++) {
			
			
			tripRate -= ps.get(i).getValue();
			
		}

		trip.setRate(tripRate);
		tripRepository.save(trip);
		ArrayList<Trip> driverTrips = tripRepository.findByDriver(driver);
		double sum = 0.0;
		for (int i = 0; i < driverTrips.size(); i++) {
			sum += driverTrips.get(i).getRate();
		}

		double driverTotalRate = (double) sum / driverTrips.size();
		driver.setRate(driverTotalRate);
		driverRepository.save(driver);
		return driverTotalRate;
	}
	
}
