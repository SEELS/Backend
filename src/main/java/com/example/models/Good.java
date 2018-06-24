package com.example.models;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name =  "good")
public class Good {
	
	@Id
	private String barcode;
		
	@Column(name = "name")
	private String name ;
	
	@Column(name = "company")
	private String company ;
	
	@Column(name = "date")
	private Date date ;
	
	@Column(name = "num_of_goods")
	private int num_of_goods;
	
	@Column(name = "deleted")
	private boolean deleted;
	
	@JsonManagedReference
	@OneToMany(mappedBy="good",cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	private Set<TripGood> goods ;

	public Good() {
		super();
	}

	

	public Set<TripGood> getTripGood() {
		return goods;
	}



	public void setTripGood(Set<TripGood> goods) {
		this.goods = goods;
	}



	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date startDate) {
		this.date = startDate;
	}


	public int getNum_of_goods() {
		return num_of_goods;
	}

	public void setNum_of_goods(int num_of_goods) {
		this.num_of_goods = num_of_goods;
	}



	public boolean isDeleted() {
		return deleted;
	}



	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}


	
	

	
	
	

}
