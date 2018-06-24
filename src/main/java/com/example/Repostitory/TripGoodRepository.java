package com.example.Repostitory;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.example.models.Good;
import com.example.models.Trip;
import com.example.models.TripGood;

public interface TripGoodRepository extends CrudRepository<TripGood, Long>
{

	ArrayList<TripGood>  findAllByTrip(Trip trip);
	ArrayList<TripGood> findAllByGood(Good good);
	public TripGood findByTripAndGood(Trip trip,Good good);
}
