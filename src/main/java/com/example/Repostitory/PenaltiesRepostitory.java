package com.example.Repostitory;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.example.models.Penalties;
import com.example.models.Trip;

public interface PenaltiesRepostitory extends CrudRepository<Penalties, Long> {

	public ArrayList<Penalties> findByTrip (Trip trip);
}
