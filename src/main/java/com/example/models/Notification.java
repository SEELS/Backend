package com.example.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notification")
public class Notification {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "content")
	private String content;
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name="trip_id_1")
	private Trip trip_id_1;
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name="trip_id_2")
	private Trip trip_id_2;
	
	@Column(name = "deleted")
	private boolean deleted;
	
	//zero means not seen yet ... 1 means seen
	@Column(name = "seen")
	private boolean seen;

	public Notification() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Notification(long id, String content, Trip trip_id_1, Trip trip_id_2, boolean deleted, boolean seen) {
		super();
		this.id = id;
		this.content = content;
		this.trip_id_1 = trip_id_1;
		this.trip_id_2 = trip_id_2;
		this.deleted = deleted;
		this.seen = seen;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Trip getTrip_id_1() {
		return trip_id_1;
	}

	public void setTrip_id_1(Trip trip_id_1) {
		this.trip_id_1 = trip_id_1;
	}

	public Trip getTrip_id_2() {
		return trip_id_2;
	}

	public void setTrip_id_2(Trip trip_id_2) {
		this.trip_id_2 = trip_id_2;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}
	
	
	
	

}
