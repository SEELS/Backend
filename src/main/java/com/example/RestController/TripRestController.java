package com.example.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.Repostitory.DriverRepository;
import com.example.Repostitory.GoodRepository;
import com.example.Repostitory.LocationRepository;
import com.example.Repostitory.RoadRepository;
import com.example.Repostitory.TripGoodRepository;
import com.example.Repostitory.TripLocationRepository;
import com.example.Repostitory.TripRepository;
import com.example.Repostitory.TruckRepository;

import com.example.models.Driver;
import com.example.models.Good;
import com.example.models.Location;
import com.example.models.Road;
import com.example.models.Trip;
import com.example.models.TripGood;
import com.example.models.TripLocation;
import com.example.models.Truck;

@RestController
@CrossOrigin(origins = "*")
public class TripRestController {
	@Autowired
	private TripRepository tripRepository;
	
	@Autowired
	private TripLocationRepository tripLocationRepository;
	@Autowired
	private RoadRepository roadRepository;
	
	@Autowired
	private GoodRepository goodRepository;
	
	@Autowired
	private TripGoodRepository tripGoodRepository;

	@Autowired
	private TruckRepository truckRepository;

	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private LocationRepository locationRepository;

	//Modified by Mariam 
	//Modified By Sameh
@RequestMapping(value = "/saveTrip/{truck_id}/{driver_id}/{parent_id}/{road_id}/{date}/{good}", method = RequestMethod.GET)
	public Map<String, String> saveTrip(@PathVariable String truck_id, @PathVariable String date,
			@PathVariable long driver_id, @PathVariable long parent_id, @PathVariable long road_id,@PathVariable String good) {
		Map<String, String> res = new HashMap<>();
		int flag=0;
		String bar="";
		if(truckRepository.findOne(truck_id)!=null)
		{
			Truck truck = truckRepository.findOne(truck_id);
			Road road = roadRepository.findOne(road_id);
			if (truck == null) {
				res.put("Error", "there's No Truck with that Id");
			} 
			else 
			{
				if (road == null) 
				{
					res.put("Error", "there's No Road with that Id");
				}
				else
				{
					ArrayList<Location> roadLocations=locationRepository.findByDeletedAndRoadOrderByTimeDesc(false,road);
					if(roadLocations.size()>=2)
					{
						if(driverRepository.findOne(driver_id)!=null)
						{
							Driver driver = driverRepository.findOne(driver_id);
							Date date_ = getDate(date);
							Trip trip = new Trip();
							trip.setRate(5.0);
							trip.setDate(date_);
							trip.setDriver(driver);
							trip.setDestination(roadLocations.get(1));
							trip.setSource(roadLocations.get(0));
							trip.setParent(parent_id);
							trip.setTruck(truck);
							trip.setRoad(road);
							trip.setState(1);
							//check if these bar codes are existed	
							String[] barcode=good.split(",");
							for(int i=0;i<barcode.length;i++)
							{
								String[] CountsWithGoods=barcode[i].split(":");
								Good g=goodRepository.findOne(CountsWithGoods[0]);
								if(g==null)
								{
									flag=3;
									bar=CountsWithGoods[0];
									break;
								}
							}
							if(flag==3)
							{
					    		res.put("Error", "There is no good with this barcode: "+bar);
							}
							else
							{

								//check of availability 
								for(int i=0;i<barcode.length;i++)
								{
									String[] CountsWithGoods=barcode[i].split(":");
									Good g=goodRepository.findOne(CountsWithGoods[0]);
							    	int num=g.getNum_of_goods();
							    	int newNum=Integer.parseInt(CountsWithGoods[1]);
							    	if(newNum>num)
							    	{
							    		flag=3;
							    		bar=g.getBarcode();
							    		break;
							    	}
								}
								if(flag==3)
								{
						    		res.put("Error", "There is not available goods for this barcode: "+bar);
								}
								else
								{
									if (tripRepository.save(trip) != null) 
									{
										for(int i=0;i<barcode.length;i++)
										{
											String[] CountsWithGoods=barcode[i].split(":");
											TripGood tripGood=new TripGood();
											tripGood.setTrip(trip);
									    	Good g=goodRepository.findOne(CountsWithGoods[0]);
									    	tripGood.setGood(g);
								    		tripGood.setNum_of_goods(Integer.parseInt(CountsWithGoods[1]));
									    	tripGood.setScan_in_num_of_goods(0);
									    	tripGood.setScan_out_num_of_goods(0);
									    	tripGood.setState(0);
									    	if (tripGoodRepository.save(tripGood) == null) {
									    		flag=1;
									    		break;
									    	}
									    	else
									    	{
									    		int newNum=Integer.parseInt(CountsWithGoods[1]);
									    		int avail=g.getNum_of_goods()-newNum;
									    		g.setNum_of_goods(avail);
									    		if(goodRepository.save(g)==null)
									    		{
									    			flag=2;
									    			break;
									    		}
									    	}
										   
										}
										if(flag==1)
										{
								    		res.put("Error", "Connection Error by saving Trip Goods");
										}
										else if(flag==0)
										{
								    		res.put("Success", "Trip is added");
										}
										else if(flag==2)
										{
								    		res.put("Error", "Connection Error by saving Goods with new number of goods");
										}
									}
									else 
									{
										res.put("Error", "Connection Error by saving Trip");
									}
								}	
							}
						}
						else
						{
							res.put("Error", "Driver is Not found");
						}
					}
					else
					{
						res.put("Error", "There is no locations for this road");
					}
				}
			}
			
		}
		else
		{
			res.put("Error","There is no truck with this id");
		}
		
		return res;
	}
	
	@RequestMapping(value = "/returnTrip/{trip_id}", method = RequestMethod.GET)
	public Map<String,Object> saveTripRoad(@PathVariable long trip_id) {
		Map<String, Object> res = new HashMap<>();
		if(tripRepository.findOne(trip_id)!=null)
		{
			Trip trip = tripRepository.findOne(trip_id);
			Road road=trip.getRoad();
			if(locationRepository.findByDeletedAndRoadOrderByTimeDesc(false,road).size()>2)
				res.put("Success", locationRepository.findByDeletedAndRoadOrderByTimeDesc(false,road));
			else
				res.put("Error","There are no locations for this trip ");
		}
		return res;
		
	}

	@RequestMapping(value = "/driverTrip/{driverId}", method = RequestMethod.GET)
	public Map<String, Object> getDriverTrip(@PathVariable long driverId) {
		Map<String, Object> res = new HashMap<>();
		Driver driver = driverRepository.findOne(driverId);
		if (driver == null) {
			res.put("Error", "There's no driver with that Id");
		} else {
//			DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
//			Date date = new Date();
//			String temp =dateFormat.format(date);
//			Date mydate = getDate(temp);
//			ArrayList<Trip> trips = tripRepository.findByDriverAndDeletedAndDateGreaterThanEqual(driver, false, mydate);
			ArrayList<Trip> trips = tripRepository.findByDriverAndDeletedAndState(driver, false, 1);
			if (trips == null) {
				res.put("Error", "There's no Trips for that Driver");
			} 
			else if(trips.isEmpty()) {
				res.put("Error", "There's no Trips for that Driver");
			}
			else {
				res.put("Success", trips.get(0).getTrip_id());
			}
		}
		return res;
	}
	@RequestMapping(value = "/driverCompletedTrip/{driverId}", method = RequestMethod.GET)
	public Map<String, Object> getDriverCompletedTrip(@PathVariable long driverId) {
		Map<String, Object> res = new HashMap<>();
		Driver driver = driverRepository.findOne(driverId);
		if (driver == null) {
			res.put("Error", "There's no driver with that Id");
		} else {
			ArrayList<Trip> trips = tripRepository.findByDriverAndDeletedAndState(driver, false, 0);
			if (trips == null) {
				res.put("Error", "There's no Trips for that Driver");
			} 
			else if(trips.isEmpty()) {
				res.put("Error", "There's no Trips for that Driver");
			}
			else {
				res.put("Success", trips);
			}
		}
		return res;
	}



	@RequestMapping(value = "/startTrip/{driverId}/{tripId}", method = RequestMethod.GET)
	public Map<String, Object> startTrip(@PathVariable long driverId, @PathVariable long tripId) {
		Map<String, Object> res = new HashMap<>();
		Driver driver = driverRepository.findOne(driverId);
		if (driver == null) {
			res.put("Error", "There's no driver with that Id");
		} else {
			Trip trip = tripRepository.findOne(tripId);
			if (trip == null) {
				res.put("Error", "There's no Trip for that Driver");
			} else {
				Truck truck = trip.getTruck();
				Map<String, String> temp = SetDriverToTruck(truck.getId(), driverId);
				if (temp.containsKey("Error"))
					res.put("Error", temp.get("Error"));
				else {
					temp = changeTruckstate(truck.getId(), true);
					if (temp.containsKey("Error"))
						res.put("Error", temp.get("Error"));
					else {
						if(trip.getState()==1 )
						{
							trip.setState(2);
							if(tripRepository.save(trip)!=null)
							{
								
								if(locationRepository.findByDeletedAndRoadOrderByTimeDesc(false,trip.getRoad()).size()>2)
								{
									res.put("Success", 0);
								}
								else
									res.put("Success",trip.getRoad().getId() );
									
							}
							else
								res.put("Error", "Error Conection to Server");
						}
						else
							res.put("Error", "this trip is completed ");
					}
				}

			}
		}
		return res;
	}

	@RequestMapping(value = "/endTrip/{driverId}/{tripId}", method = RequestMethod.GET)
	public Map<String, Object> endTrip(@PathVariable long driverId, @PathVariable long tripId) {
		Map<String, Object> res = new HashMap<>();
		Driver driver = driverRepository.findOne(driverId);
		if (driver == null) {
			res.put("Error", "There's no driver with that Id");
		} else {
			Trip trip = tripRepository.findOne(tripId);
			if (trip == null) {
				res.put("Error", "There's no Trip for that Driver");
			} else {
				Truck truck = trip.getTruck();
				Map<String, String> temp = SetDriverToTruck(truck.getId(), 0);
				if (temp.containsKey("Error"))
					res.put("Error", temp.get("Error"));
				else {
					temp = changeTruckstate(truck.getId(), false);
					if (temp.containsKey("Error"))
						res.put("Error", temp.get("Error"));
					else {
						if(trip.getState()==0)
						{
							res.put("Error", "This Trip is Ended");
						}
						else
						{
							//change state of good 
							trip.setState(0);
							if(tripRepository.save(trip)!=null)
								res.put("Success", "Done!!");
							else
								res.put("Error", "Error Conection to Server");
						}
					}
				}

			}
		}
		return res;
	}

	@RequestMapping(value = "/getAllTrips", method = RequestMethod.GET)
	public Map<String,Object> getAllTrips() {
		
		Map<String,Object> res = new HashMap<>();
		if(tripRepository.findByDeleted(false)!=null)
		{
			res.put("Success", tripRepository.findByDeleted(false));
		}
		else
		{
			res.put("Error", "There are no Trips saved");
		}
			
		return res;
	}

	@RequestMapping(value = "/getTrip/{trip_id}", method = RequestMethod.GET)
	public Map<String ,Object> getTrip(@PathVariable long trip_id) {
		Map<String,Object> res = new HashMap<>();
		if (tripRepository.findOne(trip_id) == null) {
			res.put("Error", "There is no trip with this id!");
		}
		else
		{
			Trip trip = tripRepository.findOne(trip_id);
			if (trip.isDeleted() == true) {
				res.put("Error", "This trip is deleted!");
			}
			else
			{
				res.put("Success", trip);
			}
		}
		
		return res;
	}

	@RequestMapping(value = "/deleteAllTrips", method = RequestMethod.GET)
	public Map<String,Object> deleteAllTrips() {
		Map<String,Object> res = new HashMap<>();
		boolean flag=true;
		if(tripRepository.findAll()!=null)
		{
			ArrayList<Trip> trips = (ArrayList<Trip>) tripRepository.findAll();
			for (int i = 0; i < trips.size(); i++) {
				trips.get(i).setDeleted(true);
				if (tripRepository.save(trips.get(i)) == null)
					flag=false;
			}
				if(flag==true)
					res.put("Success", "All trips are deleted!");
				else
					res.put("Error", "Connection Error!");
		}
		else
		{
			res.put("Error", "There are no trips saved!");
		}
		return res;
	}

	@RequestMapping(value = "/deleteTrip/{trip_id}", method = RequestMethod.GET)
	public Map<String ,Object> deleteTrip(@PathVariable long trip_id) {
		Map<String,Object> res = new HashMap<>();
		if (tripRepository.findOne(trip_id) == null) {
			res.put("Error","There is no trip with this id");
		} else {
			Trip trip = tripRepository.findOne(trip_id);
			trip.setDeleted(true);
			if (tripRepository.save(trip) != null)
				res.put("Success", "This trip is deleted!");
			else
				res.put("Error", "Connection Error!");
		}
		return res;
	}
	
	@RequestMapping(value = "/tripLocations/{trip_id}", method = RequestMethod.GET)
	public Map<String,Object> TripLocations(@PathVariable long trip_id) {
		Map<String,Object> res = new HashMap<>();
		Trip trip = tripRepository.findOne(trip_id);
		if (trip == null) {
			res.put("Error", "Error Conection to Server");
		} else {
			
			//error in ArrayList
			ArrayList<TripLocation> locations = tripLocationRepository.findAllLByTrip(trip);


			if(locations.isEmpty())
			{
				res.put("Error", "There's No Locations to this Trip");
			}
			else
			{
				res.put("Success", locations);
			}
		}
		return res;
	}
	
	@RequestMapping(value = "/getAvaliablityOfNumOfGoods/{barcode}", method = RequestMethod.GET)
	public Map<String,Object> getAvaliablityOfNumOfGoods(@PathVariable String barcode) {
		Map<String,Object> res = new HashMap<>();
		Good good=goodRepository.findOne(barcode);
		if(good==null)
		{
			res.put("Error", "There isn't good!");
		}
		else
		{
			int avail=good.getNum_of_goods();
			res.put("Success", avail);
		}
		return res;
	}
	
	public static boolean isValidDate(String inDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}
	
	public static Date getDate(String date)
	{
		if(isValidDate(date))
		{
			DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
			Date Date = null;
			try {
				Date = df.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Date;
		}
		return null;
	}


	public Map<String, String> SetDriverToTruck(String Truck_id,long driver_id) {
		Map<String, String> res = new HashMap<>();
		if (driver_id == 0) {
			Truck truck = truckRepository.findOne(Truck_id);
			if (truck == null) {
				res.put("Error", "truck Not found");
			} else {
				truck.setDriver(null);
				if (truckRepository.save(truck) != null)
					res.put("Success", "Done !!");
				else
					res.put("Error", "Error Update in dataBase");
			}
		} else {
			Driver driver = driverRepository.findOne(driver_id);
			if (driver == null) {
				res.put("Error", "Driver Not found");
			} else {
				Truck truck = truckRepository.findOne(Truck_id);
				if (truck == null) {
					res.put("Error", "truck Not found");
				} else {
					truck.setDriver(driver);
					if (truckRepository.save(truck) != null)
						res.put("Success", "Done !!");
					else
						res.put("Error", "Error Update in dataBase");
				}
			}
		}
		return res;
	}

	public Map<String, String> changeTruckstate(String Truck_id,boolean state) {
		Map<String, String> res = new HashMap<>();
		Truck truck = truckRepository.findOne(Truck_id);
		if (truck == null) {
			res.put("Error", "Truck Not found");
		} else {
			truck.setActive(state);
			if (truckRepository.save(truck) == null) {
				res.put("Error", "Error in update in database !!");
			} else
				res.put("Success", "Done !!");
		}
		return res;
	}
	
	
	
	
}
