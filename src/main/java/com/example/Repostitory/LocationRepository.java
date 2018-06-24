package com.example.Repostitory;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.example.models.Driver;
import com.example.models.Location;
import com.example.models.Road;
import com.example.models.Truck;

public interface LocationRepository extends CrudRepository<Location, Long> {
	public ArrayList<Location> findByDriver(Driver driver);
	
	public Location findFirstByDriverOrderByIdDesc(Driver driver);
	public Location findFirstByTruckOrderByIdDesc(Truck truck);
	public ArrayList<Location> findByDeletedAndRoadOrderByTimeDesc(boolean deleted,Road road);
	public ArrayList<Location> findByRoadIsNotNull();
	public ArrayList <Location> findAllByDeleted(boolean deleted);
	ArrayList<Location> findAllByDriver(Driver driver);
}
