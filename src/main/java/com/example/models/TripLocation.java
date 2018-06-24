package com.example.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "triplocation") 
public class TripLocation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="location_id")
	private Location location;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="trip_id")
	private Trip trip;

	public Location getLocation() {
		return location;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public TripLocation(long id,Location location, Trip trip) {
	
		this.id=id;
		this.location = location;
		this.trip = trip;
	}
	
	public TripLocation()
	{
		super();
	}

}
