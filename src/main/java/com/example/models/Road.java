package com.example.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "road")
public class Road {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	

	@Column(name = "name")
	private String name;
	
	@Column(name = "state")
	private double state;
	
	@Column(name = "deleted")
	private boolean deleted;
	
	

	public Road() {
		super();
	
	}

	public Road(long road_id, double state ) {
		super();
		this.id = road_id;
		this.state = state;
	}
	
	public long getRoad_id() {
		return id;
	}

	public void setRoad_id(long road_id) {
		this.id = road_id;
	}

	public double getState() {
		return state;
	}

	public void setState(double state) {
		this.state = state;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	
	
}
