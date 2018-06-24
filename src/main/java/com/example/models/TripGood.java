package com.example.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
@Table(name = "trip_good") 
public class TripGood {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "num_of_goods")
	private int num_of_goods;
	
	@Column(name = "scan_in_num_of_goods")
	private int scan_in_num_of_goods;
	
	@Column(name = "scan_out_num_of_goods")
	private int scan_out_num_of_goods;
	
	
	//state of goods with a trip 
	//0-> Means there is no process on this good
	//1-> Means lost good
	//2-> Means delivered
	//3-> Means inside the car "On its Way"
	
	@Column(name = "state")
	private int state;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="barcode")
	private Good good;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="trip_id")
	private Trip trip;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getNum_of_goods() {
		return num_of_goods;
	}

	public void setNum_of_goods(int num_of_goods) {
		this.num_of_goods = num_of_goods;
	}

	public int getScan_in_num_of_goods() {
		return scan_in_num_of_goods;
	}

	public void setScan_in_num_of_goods(int scan_in_num_of_goods) {
		this.scan_in_num_of_goods = scan_in_num_of_goods;
	}
	
	public int getScan_out_num_of_goods() {
		return scan_out_num_of_goods;
	}

	public void setScan_out_num_of_goods(int scan_out_num_of_goods) {
		this.scan_out_num_of_goods = scan_out_num_of_goods;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public Good getGood() {
		return good;
	}

	public void setGood(Good good) {
		this.good = good;
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public TripGood(int id, Good good, Trip trip, int num_of_goods, int scan_in_num_of_goods , int scan_out_num_of_goods, int state) {
		super();
		this.id = id;
		this.good = good;
		this.trip = trip;
		this.num_of_goods=num_of_goods;
		this.scan_in_num_of_goods=scan_in_num_of_goods;
		this.scan_out_num_of_goods=scan_out_num_of_goods;
		this.state=state;
	}

	public TripGood() {
		super();
	}
	
	
}
