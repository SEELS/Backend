package com.example.RestController;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.Repostitory.GoodRepository;
import com.example.Repostitory.TripGoodRepository;
import com.example.Repostitory.TripRepository;
import com.example.Repostitory.TruckRepository;
import com.example.models.Good;
import com.example.models.Trip;
import com.example.models.TripGood;
import com.example.models.Truck;

@RestController
@CrossOrigin(origins = "*")
public class TripGoodRestController {
	
	@Autowired
	private GoodRepository goodRepository;
	@Autowired
	private TruckRepository truckRepository;
	@Autowired
	private TripGoodRepository tripGoodRepository;
	@Autowired
	private TripRepository tripRepository;
	

	@RequestMapping(value = "/scanning/{barcode}/{truck_id}/{finish_number}/{scanning_number}", method = RequestMethod.GET)
	public Map<String,Object> scanning(@PathVariable String barcode ,@PathVariable String truck_id,@PathVariable int finish_number,@PathVariable int scanning_number)
	{
		Map<String,Object> res=new HashMap<>();
		//1 scanning in ..... 2 scanning out 
		if(scanning_number==1)
		{
			res.put("Sacnning in Process",scan_in(barcode, truck_id, finish_number));
		}
		else if(scanning_number==2)
		{
			res.put("Sacnning out Process",scan_in(barcode, truck_id, finish_number));
		}
		else
		{
			res.put("Error","The number of scanning is not valid!");
		}
		
	return res;
	}
	
	public Map<String,Object> scan_in(String barcode,String truck_id,int finish_number)
	{
		Map<String,Object> res=new HashMap<>();
		if(truckRepository.findOne(truck_id)!=null)
		{
			Truck truck=truckRepository.findOne(truck_id);
			if(tripRepository.findFirstByTruckAndStateOrderByIdDesc(truck, 1)!=null)
			{
				Trip trip =tripRepository.findFirstByTruckAndStateOrderByIdDesc(truck, 1);
				if(goodRepository.findOne(barcode)!=null)
				{
					Good good=goodRepository.findOne(barcode);
					if(tripGoodRepository.findByTripAndGood(trip, good)!=null)
					{
						TripGood tripGood=tripGoodRepository.findByTripAndGood(trip, good);
						if(finish_number==1)
						{
							tripGood.setScan_in_num_of_goods(tripGood.getScan_in_num_of_goods()+1);
							if(tripGoodRepository.save(tripGood)!=null)							
								res.put("Success", "The scanning is valid");
							else
								res.put("Error", "Connection Error");
						}
						else if(finish_number==0)
						{
							if(tripGood.getScan_in_num_of_goods()<tripGood.getNum_of_goods())
							{
								res.put("Error", "Lost Goods");
								//change the state of goods lost
								//notify the Manager
								tripGood.setState(1);
								if(tripGoodRepository.save(tripGood)!=null)							
									res.put("Success", "The scanning is valid");
								else
									res.put("Error", "Connection Error");
							}
							else if(tripGood.getScan_in_num_of_goods()>tripGood.getNum_of_goods())
							{
								res.put("Error", "There is something wrong with the scanning process!");
							}
							else
							{
								tripGood.setState(3);
								if(tripGoodRepository.save(tripGood)!=null)							
									res.put("Success", "The scanning is valid");
								else
									res.put("Error", "Connection Error");
								res.put("Success", "The scanning process is valid");

							}
						}
						else
						{
							res.put("Error", "Invalid Number of finishing");
						}
					}
					else
					{
						res.put("Error", "There is no trip assigned for this good");
					}
				}
				else
				{
					res.put("Error", "There is no good saved with this barcode");
				}
			}
			else
			{
				res.put("Error", "There is no trip assigned for this truck");
			}
			
		}
		else
		{
			res.put("Error", "There is no truck with this id");
		}
		return res;
	}
	
	public Map<String,Object> scan_out(String barcode,String truck_id,int finish_number)
	{
		Map<String,Object> res=new HashMap<>();
		if(truckRepository.findOne(truck_id)!=null)
		{
			Truck truck=truckRepository.findOne(truck_id);
			if(tripRepository.findFirstByTruckAndStateOrderByIdDesc(truck, 1)!=null)
			{
				Trip trip =tripRepository.findFirstByTruckAndStateOrderByIdDesc(truck, 1);
				if(goodRepository.findOne(barcode)!=null)
				{
					Good good=goodRepository.findOne(barcode);
					if(tripGoodRepository.findByTripAndGood(trip, good)!=null)
					{
						TripGood tripGood=tripGoodRepository.findByTripAndGood(trip, good);
						if(finish_number==1)
						{
							tripGood.setScan_out_num_of_goods(tripGood.getScan_out_num_of_goods()+1);
							if(tripGoodRepository.save(tripGood)!=null)							
								res.put("Success", "The scanning is valid");
							else
								res.put("Error", "Connection Error");
						}
						else if(finish_number==0)
						{
							if(tripGood.getScan_out_num_of_goods()<tripGood.getNum_of_goods())
							{
								res.put("Error", "Lost Goods");
								//change state of goods to lost
								//notify the Manager
								tripGood.setState(1);
								if(tripGoodRepository.save(tripGood)!=null)							
									res.put("Success", "The scanning is valid");
								else
									res.put("Error", "Connection Error");
							}
							else if(tripGood.getScan_out_num_of_goods()>tripGood.getNum_of_goods())
							{
								res.put("Error", "There is something wrong with the scanning process!");
							}
							else
							{
								//change state of goods to delivered
								tripGood.setState(2);
								if(tripGoodRepository.save(tripGood)!=null)							
									res.put("Success", "The scanning is valid");
								else
									res.put("Error", "Connection Error");
								res.put("Success", "The scanning process is valid");

							}
							
						}
						else
						{
							res.put("Error", "Invalid Number of finishing");
						}
					}
					else
					{
						res.put("Error", "There is no trip assigned for this good");
					}
				}
				else
				{
					res.put("Error", "There is no good saved with this barcode");
				}
			}
			else
			{
				res.put("Error", "There is no trip assigned for this truck");
			}
			
		}
		else
		{
			res.put("Error", "There is no truck with this id");
		}
		return res;
	}
	
	
	
}
