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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.Repostitory.GoodRepository;
import com.example.Repostitory.TripGoodRepository;
import com.example.Repostitory.TripRepository;
import com.example.Repostitory.TruckRepository;
import com.example.models.Driver;
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
								Driver d=trip.getDriver();
								String tokenID=d.getToken();
								String message="There are lost goods while scanning in!";
								send_FCM_Notification(tokenID,message);
															
								//notify the Manager
								tripGood.setState(1);
								if(tripGoodRepository.save(tripGood)!=null)							
									res.put("Error", "Lost Goods");
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
								Driver d=trip.getDriver();
								String tokenID=d.getToken();
								String message="There are lost goods while scanning out!";
								send_FCM_Notification(tokenID,message);
								//change state of goods to lost
								//notify the Manager
								tripGood.setState(1);
								if(tripGoodRepository.save(tripGood)!=null)							
									res.put("Error", "Lost Goods");
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
	
	
	public Map<String,Object> send_FCM_Notification(String tokenId,String message)
	{
		String server_key="AIzaSyBrcdEhjh8S2NbfjCKzvUnxpK6PmiCYTfw";
		
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
	
	
	
	@RequestMapping (value="/getGoodTrips/{trip_id}", method=RequestMethod.GET)
	public Map<String,Object> getGoodTrips (@PathVariable long trip_id)
	{
		Map<String,Object> res = new HashMap<>();
		Trip trip = tripRepository.findOne(trip_id);
		if (trip == null)
		{
			res.put("Error", "no trip with this id");
		}
		else
		{
			ArrayList<TripGood> tripGoods = tripGoodRepository.findAllByTrip(trip);
			if(tripGoods== null)
			{
				res.put("Error","no goods for this trip");
			}
			else 
			{
				ArrayList<Good> goods = new ArrayList<Good>();
				for (int i=0;i<tripGoods.size();i++)
				{
					goods.add(tripGoods.get(i).getGood());
				}
				res.put("Success", goods);
			}
		}
		
		
		
		
		return res;
		
		
	}
	
	
	
}
