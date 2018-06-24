package com.example.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "penalties")
public class Penalties {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "value")
	private double value;
	
	
	@ManyToOne
	@JoinColumn(name="trip_id")
	private Trip trip;
	
	@ManyToOne
	@JoinColumn(name="location_id")
	private Location location;
	
	

	public Penalties() {
		super();
	}



	public Penalties(long penalties_id, String type, double value, Trip trip, Location location) {
		super();
		this.id = penalties_id;
		this.type = type;
		this.value = value;
		this.trip = trip;
		this.location = location;
	}



	public long getPenalties_id() {
		return id;
	}



	public void setPenalties_id(long penalties_id) {
		this.id = penalties_id;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public double getValue() {
		return value;
	}



	public void setValue(double value) {
		this.value = value;
	}



	public Trip getTrip() {
		return trip;
	}



	public void setTrip(Trip trip) {
		this.trip = trip;
	}



	public Location getLocation() {
		return location;
	}



	public void setLocation(Location location) {
		this.location = location;
	}
	
	

	
	
}
