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
	
	
	
	
	
	

}
