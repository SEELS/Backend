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

import com.example.Repostitory.DriverRepository;
import com.example.Repostitory.LocationRepository;
import com.example.Repostitory.PenaltiesRepostitory;
import com.example.Repostitory.TripRepository;
import com.example.models.Driver;
import com.example.models.Location;
import com.example.models.Penalties;
import com.example.models.Trip;
import com.example.models.Truck;

@RestController
@CrossOrigin(origins="*")
/* i copied every thing related to penalties here Amina*/
public class PenaltiesRestController {
	
	@Autowired
	private TripRepository tripRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private PenaltiesRepostitory penaltiesRepostitory;
	
	@Autowired 
	private DriverRepository driverRepository;
	
	//Error Free :D 
	@RequestMapping(value="/getPenaltiesByTrip/{tripId}",method=RequestMethod.GET)
	public Map<String,Object> getPenaltiesByTrip(@PathVariable long tripId)
	{
		Map<String,Object> res = new HashMap<> ();
		Trip trip = new Trip();
		if(tripRepository.findOne(tripId)==null)
			{
				res.put("Error", "no trip with this id");
			}
		else
		{	
			trip=tripRepository.findOne(tripId);
		
			ArrayList<Penalties> penalties= penaltiesRepostitory.findByTrip(trip);
			if (penalties==null)
			{
				res.put("Error", "no penalties for this trip");
			}
			else
			{
				res.put("Success",penalties);
			}
		
		}
		
		return res;
	}
	
	//Error Free :D 
	@RequestMapping (value="/getPenaltiesByDriver/{driverId}",method=RequestMethod.GET)
	public Map<String,Object> getPenaltiesByDriver(@PathVariable long driverId)
	{
		Map<String,Object> res= new HashMap<>();
		Driver driver = new Driver();
		if(driverRepository.findOne(driverId)==null)
		{
			res.put("Error", "No driver with this id");
		}
		
		else
		{
		
			driver = driverRepository.findOne(driverId);
			
			if (driver.getDeleted())
			{
				res.put("Error", "No driver with this id Deleted ! ");
			}
			else {
			ArrayList<Trip> trips = tripRepository.findByDriver(driver);
			ArrayList<Penalties> driverPenalties = new ArrayList<Penalties>();
			if(trips==null)
			{
				res.put("Error", "No trips for this driver");
			}
			else
				{
					for (int i=0 ; i<trips.size();i++)
						{
								
						driverPenalties.addAll(penaltiesRepostitory.findByTrip(trips.get(i)));
						
						}
					if (driverPenalties.size()==0)
					{
						res.put("Error", "no penalties for this driver");
					}
					else
					{
						res.put("Success",driverPenalties);
					}

				}
			}
		}
		return res;
	}
	
	
	//Error Free :D 
	@RequestMapping(value = "/tripRate/{tripId}", method = RequestMethod.GET)
	public Map<String,Object> tripRate(@PathVariable long tripId) {
		Map<String,Object> res=new HashMap<>();
		if(tripRepository.findOne(tripId)!=null)
		{
			Trip trip = tripRepository.findOne(tripId);
			double tripRate = 5.0;
			
			if(penaltiesRepostitory.findByTrip(trip)!=null)
			{
				ArrayList<Penalties> ps = penaltiesRepostitory.findByTrip(trip);
				for (int i = 0; i < ps.size(); i++) {
					
					
					tripRate -= ps.get(i).getValue();
					
				}

				trip.setRate(tripRate);
				if(tripRepository.save(trip)!=null)
				{
					res.put("Success",tripRate );
				}
				else
				{
					res.put("Error","Connection Error");
				}
			}
			else
			{
				res.put("Error","There is no penalty saved for this trip");
			}
			
		}
		else
		{
			res.put("Error", "There is no Trip!");
		}
		
		return res;
	}
	
	/* calculate penalty during trip */
	// Amina
	//Error Free :D 
	@RequestMapping(value = "/calculateSpeedPenalty/{tripId}", method = RequestMethod.GET)
	public Map<String,Object> calculateSpeedPenalty(@PathVariable long tripId) {
		double civilSpeed=90.0;
		Map<String,Object> res=new HashMap<>();

		if(tripRepository.findOne(tripId)==null)
		{
			res.put("Error","No trip with this id");
		}
		else
			
		{
			Trip trip = tripRepository.findOne(tripId);
		
		Truck truck=trip.getTruck();
		Location location = new Location();

		location=locationRepository.findFirstByTruckOrderByIdDesc(truck);
		
		
	
		Penalties p = new Penalties();
		
		double diffrence = location.getSpeed() - civilSpeed;
		double penalty = 0.0;
		for (int i = 10; i <= diffrence; i += 10) {
			penalty += 0.1;
		}
		p.setLocation(location);
		p.setTrip(trip);
		p.setType("speed");
		p.setValue(penalty);
		penaltiesRepostitory.save(p);
		if(penaltiesRepostitory.findByTrip(trip)!=null)
			res.put("driver total rate is", tripRate(tripId)); 
		else
			res.put("Error",tripRate(tripId));
		
		}
		return res;
	}
	// Amina
	
	
		//Error Free :D 
		@RequestMapping(value = "/calculateBrakePenalty/{previousSpeed}/{currentSpeed}/{tripId}", method = RequestMethod.GET)
		public Map<String,Object> calculateBrakePenalty(@PathVariable double previousSpeed, @PathVariable double currentSpeed, @PathVariable long tripId) {
			
			
			Map<String,Object> res=new HashMap<>();
			
			if (tripRepository.findOne(tripId)==null)
			{
				res.put("Error", "No trip with this id");
			}
			else
			{Trip trip = tripRepository.findOne(tripId);
			Truck truck=trip.getTruck();
			Location location = new Location();
			location=locationRepository.findFirstByTruckOrderByIdDesc(truck);
			
			double diffrence =currentSpeed - previousSpeed;

			if (diffrence >= 50) {
				Penalties p = new Penalties();
				p.setLocation(location);
				p.setTrip(trip);
				p.setType("brake");
				p.setValue(0.2);
				penaltiesRepostitory.save(p);
			
			}
			
			if(penaltiesRepostitory.findByTrip(trip)!=null)
				res.put("driver total rate is", tripRate(tripId)); 
			else
				res.put("Error",tripRate(tripId));
			
			}
			return res;

		}

}
